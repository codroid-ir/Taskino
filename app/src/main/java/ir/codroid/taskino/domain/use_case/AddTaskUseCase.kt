package ir.codroid.taskino.domain.use_case

import ir.codroid.taskino.domain.repository.TodoRepository
import javax.inject.Inject

class AddTaskUseCase @Inject constructor(
    private val repository: TodoRepository
) {

}