package com.example.flowexample

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    val countDownFlow = flow<Int> {
        var startingValue = 10
        var currentValue = startingValue
        emit(currentValue)
        while (currentValue > 0) {
            delay(1000L)
            currentValue--
            emit(currentValue)
        }
    }

    init {
        collectFlow()
    }

    private fun collectFlow() {
        viewModelScope.launch {
            countDownFlow.collectLatest { time ->
                delay(1500L)
                println("The current time $time")
            }
        }
    }

    private val _liveData = MutableLiveData("Hello World!")
    val livedata: LiveData<String> = _liveData

    private val _stateFlow = MutableStateFlow("Hello World!")
    val stateFlow = _stateFlow.asStateFlow()

    private val _shareFlow = MutableSharedFlow<String>()
    val sharedFlow = _shareFlow.asSharedFlow()

    fun triggerLiveData() {
        _liveData.value = "LiveData"
    }

    fun triggerStateFlow() {
        _stateFlow.value = "StateFlow"
    }

    fun triggerFlow(): Flow<String> {
        return flow {
            repeat(5) {
                emit("Item $it")
                delay(1000L)
            }
        }
    }

    fun triggerSharedFlow() {
        viewModelScope.launch {
            _shareFlow.emit("SharedFlow")
        }
    }
}