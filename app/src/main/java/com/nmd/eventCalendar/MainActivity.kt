package com.nmd.eventCalendar

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.nmd.eventCalendar.MainActivity.RandomEventList.Companion.createRandomEventList
import com.nmd.eventCalendar.`interface`.EventCalendarDayClickListener
import com.nmd.eventCalendar.`interface`.EventCalendarScrollListener
import com.nmd.eventCalendar.model.Day
import com.nmd.eventCalendar.model.Event
import com.nmd.eventCalendarSample.databinding.ActivityMainBinding
import com.nmd.eventCalendarSample.databinding.BottomSheetBinding
import com.nmd.eventCalendarSample.databinding.BottomSheetSingleWeekBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.activityMainMaterialToolbar)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val insets =
                windowInsets.getInsets(WindowInsetsCompat.Type.systemBars() + WindowInsetsCompat.Type.displayCutout())

            binding.activityMainAppBarLayout.updatePadding(
                left = insets.left,
                top = insets.top,
                right = insets.right
            )

            binding.activityMainFloatingActionButtonHolder.updatePadding(
                bottom = insets.bottom,
                right = insets.right
            )

            ViewCompat.onApplyWindowInsets(view, windowInsets)
        }

        initialize()
    }

    companion object {
        private var randomEventList = ArrayList<Event>()
    }

    private fun initialize() {
        with(binding) {
            activityMainProgressBar.visibility = View.VISIBLE
            activityMainEventCalendarView.visibility = View.GONE

            val year = Calendar.getInstance().get(Calendar.YEAR)
            activityMainEventCalendarView.setMonthAndYear(
                startMonth = 1, startYear = year, endMonth = 12, endYear = year
            )
            activityMainCalendarImageView.setOnClickListener {
                activityMainEventCalendarView.scrollToCurrentMonth(false, true)
            }

            activityMainShuffleImageView.setOnClickListener {
                activityMainProgressBar.visibility = View.VISIBLE
                activityMainEventCalendarView.visibility = View.GONE

                createRandomEventList(256) {
                    randomEventList = it
                    activityMainEventCalendarView.events = it
                    activityMainEventCalendarView.post {
                        activityMainProgressBar.visibility = View.GONE
                        activityMainEventCalendarView.visibility = View.VISIBLE
                    }
                }
            }

            activityMainEventCalendarView.addOnDayClickListener(object :
                EventCalendarDayClickListener {
                override fun onClick(day: Day) {
                    val eventList =
                        activityMainEventCalendarView.events.filter { it.date == day.date }
                    bottomSheet(day, eventList)
                }
            })
            activityMainEventCalendarView.addOnCalendarScrollListener(object :
                EventCalendarScrollListener {
                override fun onScrolled(month: Int, year: Int) {
                    Log.i("ECV", "Scrolled to: $month $year")
                }
            })

            if (randomEventList.isEmpty()) {
                createRandomEventList(256) {
                    randomEventList = it
                    activityMainEventCalendarView.events = it
                    activityMainEventCalendarView.post {
                        activityMainProgressBar.visibility = View.GONE
                        activityMainEventCalendarView.visibility = View.VISIBLE
                    }
                }
            } else {
                activityMainEventCalendarView.events = randomEventList
                activityMainEventCalendarView.post {
                    activityMainProgressBar.visibility = View.GONE
                    activityMainEventCalendarView.visibility = View.VISIBLE
                }
            }

            activityMainFloatingActionButtonCalendarWeekToggle.setOnClickListener {
                activityMainEventCalendarView.calendarWeekVisible =
                    !activityMainEventCalendarView.calendarWeekVisible
            }

            activityMainFloatingActionButtonSingleWeekView.setOnClickListener {
                bottomSheet2()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun bottomSheet(day: Day, eventList: List<Event>) {
        val binding = BottomSheetBinding.inflate(LayoutInflater.from(this))
        val bottomSheetDialog =
            BottomSheetDialog(this, com.nmd.eventCalendarSample.R.style.BottomSheetDialog)

        binding.bottomSheetMaterialTextView.text = day.date + " (" + eventList.size + ")"
        binding.bottomSheetNoEventsMaterialTextView.visibility =
            if (eventList.isEmpty()) View.VISIBLE else View.GONE

        binding.bottomSheetRecyclerView.adapter = SheetEventsAdapter(ArrayList(eventList))

        bottomSheetDialog.setContentView(binding.root)
        bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetDialog.behavior.skipCollapsed = true
        bottomSheetDialog.setCancelable(true)

        bottomSheetDialog.show()
    }

    private fun bottomSheet2() {
        val binding = BottomSheetSingleWeekBinding.inflate(LayoutInflater.from(this))
        val bottomSheetDialog =
            BottomSheetDialog(this, com.nmd.eventCalendarSample.R.style.BottomSheetDialog)

        binding.bottomSheetEventCalendarSingleWeekView.events =
            this@MainActivity.binding.activityMainEventCalendarView.events
        binding.bottomSheetEventCalendarSingleWeekView.addOnDayClickListener(object :
            EventCalendarDayClickListener {
            override fun onClick(day: Day) {
                bottomSheetDialog.dismiss()

                val eventList =
                    this@MainActivity.binding.activityMainEventCalendarView.events.filter { it.date == day.date }
                bottomSheet(day, eventList)
            }
        })

        bottomSheetDialog.setContentView(binding.root)
        bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetDialog.behavior.skipCollapsed = true
        bottomSheetDialog.setCancelable(true)

        bottomSheetDialog.show()
    }

    data class RandomEventList(
        var name: String,
        val color: String,
    ) {
        companion object {
            private val list = arrayListOf(
                RandomEventList("Meeting", "#e07912"),
                RandomEventList("Vacation", "#4badeb"),
                RandomEventList("Birthday Party", "#ff6f00"),
                RandomEventList("Concert", "#d500f9"),
                RandomEventList("Job Interview", "#7cb342"),
                RandomEventList("Doctor's Appointment", "#29b6f6"),
                RandomEventList("Gym Session", "#ef5350"),
                RandomEventList("Networking Event", "#ab47bc"),
                RandomEventList("Movie Night", "#ffee58"),
                RandomEventList("Dinner Date", "#26a69a"),
                RandomEventList("Business Trip", "#8d6e63"),
                RandomEventList("Charity Event", "#ff9800"),
                RandomEventList("Book Club Meeting", "#b71c1c"),
                RandomEventList("Coffee with Friends", "#9ccc65"),
                RandomEventList("Music Festival", "#7e57c2"),
                RandomEventList("Volunteering", "#78909c"),
                RandomEventList("Sports Game", "#f44336"),
                RandomEventList("Art Exhibition", "#9c27b0"),
                RandomEventList("Language Exchange", "#4caf50"),
                RandomEventList("Hiking Trip", "#cddc39"),
                RandomEventList("Yoga Class", "#26c6da"),
                RandomEventList("Baking Workshop", "#ffab00"),
                RandomEventList("Science Fair", "#6a1b9a"),
                RandomEventList("Board Game Night", "#607d8b"),
                RandomEventList("Fashion Show", "#f57c00"),
                RandomEventList("Political Rally", "#009688"),
                RandomEventList("Writing Workshop", "#ff4081"),
                RandomEventList("Tech Conference", "#1565c0"),
                RandomEventList("Wine Tasting", "#8bc34a"),
                RandomEventList("Cooking Class", "#f4511e"),
                RandomEventList("Open Mic Night", "#673ab7"),
                RandomEventList("Karaoke Night", "#ff5252"),
                RandomEventList("Outdoor Concert", "#64dd17"),
                RandomEventList("Flea Market", "#9e9e9e"),
                RandomEventList("Art Museum Tour", "#ff1744"),
                RandomEventList("Escape Room", "#00bcd4"),
                RandomEventList("Photography Workshop", "#ffd600"),
                RandomEventList("Ballet Performance", "#9fa8da"),
                RandomEventList("Fashion Design Course", "#4caf4f"),
                RandomEventList("Community Service", "#c2185b"),
                RandomEventList("Trivia Night", "#2196f3"),
                RandomEventList("Chess Tournament", "#afb42b"),
                RandomEventList("Stand-up Comedy Show", "#795548"),
                RandomEventList("Book Signing", "#e91e63"),
                RandomEventList("Potluck Party", "#689f38"),
                RandomEventList("Art Auction", "#ba68c8"),
                RandomEventList("Game Night", "#00897b"),
                RandomEventList("Beer Tasting", "#ffd54f"),
                RandomEventList("Stand-up Paddleboarding", "#0277bd"),
                RandomEventList("Charity Run", "#f57f17"),
                RandomEventList("Poetry Slam", "#f44336"),
                RandomEventList("Salsa Dancing", "#4caf50"),
                RandomEventList("Board Game Cafe", "#8bc34a"),
                RandomEventList("Movie Marathon", "#b71c1c"),
                RandomEventList("Bike Tour", "#4db6ac"),
                RandomEventList("Outdoor Yoga", "#7cb342"),
                RandomEventList("Art Walk", "#00bcd4"),
                RandomEventList("Wine and Paint Night", "#9c27b0"),
                RandomEventList("Plant Swap", "#388e3c"),
                RandomEventList("Beach Clean-up", "#009688"),
                RandomEventList("Indoor Skydiving", "#ff6f00"),
                RandomEventList("Ice Skating", "#0277bd"),
                RandomEventList("Farmers Market", "#cddc39"),
                RandomEventList("Game of Thrones Marathon", "#6d4c41"),
                RandomEventList("Soap Making Workshop", "#26a69a"),
                RandomEventList("Beer and Cheese Pairing", "#ffab00"),
                RandomEventList("Group Painting Session", "#ffa000"),
                RandomEventList("Food Truck Festival", "#f06292"),
                RandomEventList("Ghost Tour", "#7e57c2"),
                RandomEventList("Sushi Making Class", "#0091ea"),
                RandomEventList("Aquarium Visit", "#b2ff59"),
                RandomEventList("Murder Mystery Dinner", "#d500f9"),
                RandomEventList("Vintage Clothing Market", "#f57c00"),
                RandomEventList("Rock Climbing", "#6a1b9a"),
                RandomEventList("DIY Woodworking Class", "#795548"),
                RandomEventList("Meditation Retreat", "#0097a7"),
                RandomEventList("Group Bike Ride", "#d32f2f"),
                RandomEventList("Cooking Competition", "#ff5252"),
                RandomEventList("Ice Cream Social", "#00bcd4"),
                RandomEventList("Haunted House Visit", "#ba68c8"),
                RandomEventList("Photography Walk", "#4caf50"),
                RandomEventList("Beach Volleyball", "#ffa000"),
                RandomEventList("Gardening Workshop", "#4db6ac"),
                RandomEventList("Laser Tag", "#673ab7"),
                RandomEventList("Bird Watching Tour", "#ff4081"),
                RandomEventList("Movie in the Park", "#43a047"),
                RandomEventList("Cider Tasting", "#ef5350"),
                RandomEventList("Escape Game", "#29b6f6"),
                RandomEventList("Cheese Making Class", "#ffc107"),
                RandomEventList("Farm Visit", "#7cb342")
            )

            fun createRandomEventList(numRandomEvents: Int, callback: (ArrayList<Event>) -> Unit) {
                CoroutineScope(Dispatchers.IO).launch {
                    val currentDate = Calendar.getInstance()
                    val currentYear = currentDate.get(Calendar.YEAR)

                    val eventList = arrayListOf<Event>()
                    val eventsPerMonth = numRandomEvents / 12

                    for (month in 1..12) {
                        val calendar = Calendar.getInstance().apply {
                            set(currentYear, month - 1, 1)
                        }
                        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

                        val randomEvents = arrayListOf<RandomEventList>()
                        repeat(list.size) {
                            val randomEvent = list.random()
                            randomEvents.add(randomEvent)
                        }

                        val eventsForMonth = arrayListOf<Event>()
                        for (i in 1..eventsPerMonth) {
                            val randomEvent = randomEvents.random()
                            val randomDay = (1..daysInMonth).random()
                            val dateStr =
                                String.format(
                                    Locale.GERMAN,
                                    "%02d.%02d.%04d",
                                    randomDay,
                                    month,
                                    currentYear
                                )
                            val newEvent = Event(dateStr, randomEvent.name, randomEvent.color)
                            eventsForMonth.add(newEvent)
                        }

                        eventList.addAll(eventsForMonth.shuffled())
                    }

                    withContext(Dispatchers.Main) {
                        callback.invoke(eventList)
                    }
                }
            }
        }
    }

}