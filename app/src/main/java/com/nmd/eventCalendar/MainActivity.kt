package com.nmd.eventCalendar

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
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

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        initialize()
    }

    private fun initialize() {
        binding.progressBar.visibility = View.VISIBLE
        binding.eventCalendarView.visibility = View.GONE
        //binding.eventCalendarView.setMonthAndYear(4, 2023, 6, 2023)
        binding.eventCalendarViewCalendarImageView.setOnClickListener {
            binding.eventCalendarView.scrollToCurrentMonth(false)
        }

        binding.eventCalendarViewShuffleImageView.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            binding.eventCalendarView.visibility = View.GONE

            createRandomEventList(256) {
                binding.eventCalendarView.events = it
                binding.eventCalendarView.post {
                    binding.progressBar.visibility = View.GONE
                    binding.eventCalendarView.visibility = View.VISIBLE
                }
            }
        }

        binding.eventCalendarView.addOnDayClickListener(object :
            EventCalendarDayClickListener {
            override fun onClick(day: Day) {
                val eventList = binding.eventCalendarView.events.filter { it.date == day.date }
                bottomSheet(day, eventList)
            }
        })
        binding.eventCalendarView.addOnCalendarScrollListener(object : EventCalendarScrollListener {
            override fun onScrolled(month: Int, year: Int) {
                Log.i("ECV", "Scrolled to: $month $year")
            }
        })

        createRandomEventList(256) {
            binding.eventCalendarView.events = it
            binding.eventCalendarView.post {
                binding.progressBar.visibility = View.GONE
                binding.eventCalendarView.visibility = View.VISIBLE
            }
        }

        binding.floatingActionButton.setOnClickListener {
            bottomSheet2()
        }

    }

    @SuppressLint("SetTextI18n")
    private fun bottomSheet(day: Day, eventList: List<Event>) {
        val binding =
            BottomSheetBinding.inflate(LayoutInflater.from(this))
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
        val binding =
            BottomSheetSingleWeekBinding.inflate(LayoutInflater.from(this))
        val bottomSheetDialog =
            BottomSheetDialog(this, com.nmd.eventCalendarSample.R.style.BottomSheetDialog)

        binding.bottomSheetEventCalendarSingleWeekView.events =
            this@MainActivity.binding.eventCalendarView.events
        binding.bottomSheetEventCalendarSingleWeekView.addOnDayClickListener(object :
            EventCalendarDayClickListener {
            override fun onClick(day: Day) {
                bottomSheetDialog.dismiss()

                val eventList =
                    this@MainActivity.binding.eventCalendarView.events.filter { it.date == day.date }
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
            private val list = ArrayList<RandomEventList>().apply {
                add(RandomEventList("Meeting", "#e07912"))
                add(RandomEventList("Vacation", "#4badeb"))
                add(RandomEventList("Birthday Party", "#ff6f00"))
                add(RandomEventList("Concert", "#d500f9"))
                add(RandomEventList("Job Interview", "#7cb342"))
                add(RandomEventList("Doctor's Appointment", "#29b6f6"))
                add(RandomEventList("Gym Session", "#ef5350"))
                add(RandomEventList("Networking Event", "#ab47bc"))
                add(RandomEventList("Movie Night", "#ffee58"))
                add(RandomEventList("Dinner Date", "#26a69a"))
                add(RandomEventList("Business Trip", "#8d6e63"))
                add(RandomEventList("Charity Event", "#ff9800"))
                add(RandomEventList("Book Club Meeting", "#b71c1c"))
                add(RandomEventList("Coffee with Friends", "#9ccc65"))
                add(RandomEventList("Music Festival", "#7e57c2"))
                add(RandomEventList("Volunteering", "#78909c"))
                add(RandomEventList("Sports Game", "#f44336"))
                add(RandomEventList("Art Exhibition", "#9c27b0"))
                add(RandomEventList("Language Exchange", "#4caf50"))
                add(RandomEventList("Hiking Trip", "#cddc39"))
                add(RandomEventList("Yoga Class", "#26c6da"))
                add(RandomEventList("Baking Workshop", "#ffab00"))
                add(RandomEventList("Science Fair", "#6a1b9a"))
                add(RandomEventList("Board Game Night", "#607d8b"))
                add(RandomEventList("Fashion Show", "#f57c00"))
                add(RandomEventList("Political Rally", "#009688"))
                add(RandomEventList("Writing Workshop", "#ff4081"))
                add(RandomEventList("Tech Conference", "#1565c0"))
                add(RandomEventList("Wine Tasting", "#8bc34a"))
                add(RandomEventList("Cooking Class", "#f4511e"))
                add(RandomEventList("Open Mic Night", "#673ab7"))
                add(RandomEventList("Karaoke Night", "#ff5252"))
                add(RandomEventList("Outdoor Concert", "#64dd17"))
                add(RandomEventList("Flea Market", "#9e9e9e"))
                add(RandomEventList("Art Museum Tour", "#ff1744"))
                add(RandomEventList("Escape Room", "#00bcd4"))
                add(RandomEventList("Photography Workshop", "#ffd600"))
                add(RandomEventList("Ballet Performance", "#9fa8da"))
                add(RandomEventList("Fashion Design Course", "#4caf4f"))
                add(RandomEventList("Community Service", "#c2185b"))
                add(RandomEventList("Trivia Night", "#2196f3"))
                add(RandomEventList("Chess Tournament", "#afb42b"))
                add(RandomEventList("Stand-up Comedy Show", "#795548"))
                add(RandomEventList("Book Signing", "#e91e63"))
                add(RandomEventList("Potluck Party", "#689f38"))
                add(RandomEventList("Art Auction", "#ba68c8"))
                add(RandomEventList("Game Night", "#00897b"))
                add(RandomEventList("Beer Tasting", "#ffd54f"))
                add(RandomEventList("Stand-up Paddleboarding", "#0277bd"))
                add(RandomEventList("Charity Run", "#f57f17"))
                add(RandomEventList("Poetry Slam", "#f44336"))
                add(RandomEventList("Salsa Dancing", "#4caf50"))
                add(RandomEventList("Board Game Cafe", "#8bc34a"))
                add(RandomEventList("Movie Marathon", "#b71c1c"))
                add(RandomEventList("Bike Tour", "#4db6ac"))
                add(RandomEventList("Outdoor Yoga", "#7cb342"))
                add(RandomEventList("Art Walk", "#00bcd4"))
                add(RandomEventList("Wine and Paint Night", "#9c27b0"))
                add(RandomEventList("Plant Swap", "#388e3c"))
                add(RandomEventList("Beach Clean-up", "#009688"))
                add(RandomEventList("Indoor Skydiving", "#ff6f00"))
                add(RandomEventList("Ice Skating", "#0277bd"))
                add(RandomEventList("Farmers Market", "#cddc39"))
                add(RandomEventList("Game of Thrones Marathon", "#6d4c41"))
                add(RandomEventList("Soap Making Workshop", "#26a69a"))
                add(RandomEventList("Beer and Cheese Pairing", "#ffab00"))
                add(RandomEventList("Group Painting Session", "#ffa000"))
                add(RandomEventList("Food Truck Festival", "#f06292"))
                add(RandomEventList("Ghost Tour", "#7e57c2"))
                add(RandomEventList("Sushi Making Class", "#0091ea"))
                add(RandomEventList("Aquarium Visit", "#b2ff59"))
                add(RandomEventList("Murder Mystery Dinner", "#d500f9"))
                add(RandomEventList("Vintage Clothing Market", "#f57c00"))
                add(RandomEventList("Rock Climbing", "#6a1b9a"))
                add(RandomEventList("DIY Woodworking Class", "#795548"))
                add(RandomEventList("Meditation Retreat", "#0097a7"))
                add(RandomEventList("Group Bike Ride", "#d32f2f"))
                add(RandomEventList("Cooking Competition", "#ff5252"))
                add(RandomEventList("Ice Cream Social", "#00bcd4"))
                add(RandomEventList("Haunted House Visit", "#ba68c8"))
                add(RandomEventList("Photography Walk", "#4caf50"))
                add(RandomEventList("Beach Volleyball", "#ffa000"))
                add(RandomEventList("Gardening Workshop", "#4db6ac"))
                add(RandomEventList("Laser Tag", "#673ab7"))
                add(RandomEventList("Bird Watching Tour", "#ff4081"))
                add(RandomEventList("Movie in the Park", "#43a047"))
                add(RandomEventList("Cider Tasting", "#ef5350"))
                add(RandomEventList("Escape Game", "#29b6f6"))
                add(RandomEventList("Cheese Making Class", "#ffc107"))
                add(RandomEventList("Farm Visit", "#7cb342"))
            }

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
                                String.format("%02d.%02d.%04d", randomDay, month, currentYear)
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