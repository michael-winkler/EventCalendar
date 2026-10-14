package com.nmd.eventCalendar.compose.util

import java.util.concurrent.atomic.AtomicInteger

// ---------------------------------------------------------------------------------------------
// Platform boundary (JVM/Android): thread-safe counter backing the auto-generated Event ids.
//
// Kept in its own file so the rest of the library stays free of java.* APIs. Under Kotlin
// Multiplatform this becomes an `expect`/`actual` (or uses kotlin.concurrent.atomics.AtomicInt).
// ---------------------------------------------------------------------------------------------

private val processUniqueIdCounter = AtomicInteger(1)

/**
 * Returns the next id that is unique within the current process (starting at 1). Thread-safe.
 */
internal fun nextProcessUniqueId(): Int = processUniqueIdCounter.getAndIncrement()
