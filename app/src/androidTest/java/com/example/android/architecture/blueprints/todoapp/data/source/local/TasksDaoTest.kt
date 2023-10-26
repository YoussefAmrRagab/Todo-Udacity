package com.example.android.architecture.blueprints.todoapp.data.source.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.android.architecture.blueprints.todoapp.data.Task
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class TasksDaoTest {

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: ToDoDatabase

    @Before
    fun initDb() {
        // Using an in-memory database so that the information stored here disappears when the
        // process is killed.
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ToDoDatabase::class.java
        ).build()
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun insertTaskAndGetById() = runBlockingTest {
        val task = Task("title", "description")
        database.taskDao().insertTask(task)

        val loaded = database.taskDao().getTaskById(task.id)
        assertThat(loaded as Task, notNullValue())
        assertThat(loaded.id, `is`(task.id))
        assertThat(loaded.title, `is`(task.title))
        assertThat(loaded.description, `is`(task.description))
        assertThat(loaded.isCompleted, `is`(task.isCompleted))
    }

    @Test
    fun updateTaskAndGetById() {
        val task = Task("title", "description")

        database.taskDao().insertTask(task)

        val updatedTask = Task(
            id = task.id,
            title = "updated title",
            description = "updated description",
            isCompleted = true
        )

        database.taskDao().updateTask(updatedTask)

        val loaded = database.taskDao().getTaskById(task.id)
        assertThat(loaded as Task, notNullValue())
        assertThat(loaded.id, `is`(task.id))
        assertThat(loaded.title, not(task.title))
        assertThat(loaded.description, not((task.description)))
        assertThat(loaded.isCompleted, not((task.isCompleted)))
        assertThat(loaded.title, `is`(updatedTask.title))
        assertThat(loaded.description, `is`(updatedTask.description))
        assertThat(loaded.isCompleted, `is`(updatedTask.isCompleted))
    }
}