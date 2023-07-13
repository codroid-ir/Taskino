package ir.codroid.taskino.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.codroid.taskino.data.model.Priority
import ir.codroid.taskino.data.model.ToDoTask
import ir.codroid.taskino.repository.TodoRepository
import ir.codroid.taskino.ui.theme.MAX_TITLE_LENGTH
import ir.codroid.taskino.util.Action
import ir.codroid.taskino.util.RequestState
import ir.codroid.taskino.util.SearchAppbarState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(private val repository: TodoRepository) : ViewModel() {

    val searchAppbarState: MutableState<SearchAppbarState> =
        mutableStateOf(SearchAppbarState.CLOSED)
    val searchAppbarTextState: MutableState<String> = mutableStateOf("")
    private val _taskList =
        MutableStateFlow<RequestState<List<ToDoTask>>>(RequestState.NotInitialize)
    val taskList = _taskList.asStateFlow()

    val id: MutableState<Int> = mutableStateOf(0)
    val title: MutableState<String> = mutableStateOf("")
    val description: MutableState<String> = mutableStateOf("")
    val priority: MutableState<Priority> = mutableStateOf(Priority.LOW)
    val action: MutableState<Action> = mutableStateOf(Action.NO_ACTION)

    private val _selectedTask =
        MutableStateFlow<RequestState<ToDoTask?>>(RequestState.NotInitialize)
    val selectedTask = _selectedTask.asStateFlow()

    fun getAllTask() {
        _taskList.value = RequestState.Loading
        try {

            viewModelScope.launch {
                repository.getAllTasks.collect {
                    _taskList.emit(RequestState.Success(it))
                }
            }
        } catch (e: Exception) {
            _taskList.value = RequestState.Error(e)
        }
    }

    fun getSelectedTask(taskId: Int) {
        try {

            viewModelScope.launch {
                repository.getSelectedTask(taskId = taskId).collect { task ->
                    _selectedTask.emit(RequestState.Success(task))
                }
            }
        } catch (e: Exception) {
            _selectedTask.value = RequestState.Error(e)
        }
    }

    fun updateTaskFiled(selectedTask: ToDoTask?) {
        if (selectedTask != null) {
            id.value = selectedTask.id
            title.value = selectedTask.title
            description.value = selectedTask.description
            priority.value = selectedTask.priority
        } else {
            id.value = 0
            title.value = ""
            description.value = ""
            priority.value = Priority.LOW
        }
    }

    fun updateTitle(newTitle: String) {
        if (newTitle.length < MAX_TITLE_LENGTH)
            title.value = newTitle
    }

    fun validateFields() =
        title.value.isNotEmpty() && description.value.isNotEmpty()

    private fun addTask() {
        val toDoTask = ToDoTask(
            title = title.value,
            description = description.value,
            priority = priority.value
        )
        viewModelScope.launch(Dispatchers.IO) {
            repository.addTask(toDoTask = toDoTask)
        }
    }

    fun handleDatabaseAction(action: Action) {
        when (action) {
            Action.ADD -> {
                addTask()
            }

            Action.UPDATE -> {}
            Action.DELETE -> {}
            Action.DELETE_ALL -> {}
            Action.UNDO -> {}
            else -> {}
        }
        this.action.value = Action.NO_ACTION
    }
}