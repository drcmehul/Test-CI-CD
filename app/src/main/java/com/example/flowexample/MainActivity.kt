package com.example.flowexample

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.flowexample.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]

        /*lifecycleScope.launch {
            val count = mainViewModel.countDownFlow
                .filter { time ->
                    time % 2 == 0
                }
                .map { time ->
                    time * time
                }
                .onEach {
                    println("on each $it")
                }
                .count { time ->
                    time % 2 == 0
                }
            binding.tvCurrentTime.text = count.toString()
        }*/

        /**
         * accumulator with first and second value */
        /*lifecycleScope.launch {
            val reducedResult = mainViewModel.countDownFlow
                .reduce { accumulator, value ->
                    Log.e("accumulator $accumulator ", " - value $value")
                    accumulator + value
                }
            binding.tvCurrentTime.text = reducedResult.toString()
        }*/

        /**
         * accumulator with fold initial value */
        /*lifecycleScope.launch {
            val reducedResult = mainViewModel.countDownFlow
                .fold(100) { accumulator, value ->
                    Log.e("accumulator $accumulator ", " - value $value")
                    accumulator + value
                }
            binding.tvCurrentTime.text = reducedResult.toString()
        }*/

        binding.btnLiveData.setOnClickListener {
            mainViewModel.triggerLiveData()
        }

        binding.btnStateFlow.setOnClickListener {
            mainViewModel.triggerStateFlow()
        }

        binding.btnFlow.setOnClickListener {
            lifecycleScope.launch {
                mainViewModel.triggerFlow().collectLatest {
                    binding.tvFlow.text = it
                }
            }
        }

        binding.btnSharedFlow.setOnClickListener {
            mainViewModel.triggerSharedFlow()
        }

        binding.btnHigherOrderFunction.setOnClickListener {
            /*higherOrderAddition("Mehul", "Gajjar") { firstName, lastName ->
                "$firstName $lastName"
            }*/
            /*val names = listOf("Harvey","Mike","Rachel","Dona","Peter")
            printFilteredNames(names){
                it.startsWith("H")
            }*/
            /*rollDice(1..99, 3) { result ->
                binding.tvHigherOrder.text = result.toString()
            }*/
            /*rollDick {
                binding.tvHigherOrder.text = it.toString()
            }*/
            /*rollDick {
                lifecycleScope.launch {
                    binding.tvHigherOrder.text = it.toString()
                }
            }*/
            //guide()
            crossInLineGuild()
        }

        binding.btnFlowLongRunningTask.setOnClickListener {
            lifecycleScope.launch {
                flowLongRunningTask()
            }
        }

        subscribeToObserver(mainViewModel)
    }


    private fun subscribeToObserver(viewModel: MainViewModel) {
        viewModel.livedata.observe(this) {
            binding.tvLiveData.text = it
        }

        lifecycleScope.launch {
            viewModel.stateFlow.collectLatest {
                binding.tvStateFlow.text = it
                //Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT).show()
            }
        }

        lifecycleScope.launch {
            viewModel.sharedFlow.collectLatest {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * higher order function */
    private fun higherOrderAddition(
        firstName: String,
        lastname: String,
        returnFunction: (String, String) -> String
    ) {
        //returnFunction(firstName, lastname)
        binding.tvHigherOrder.text = returnFunction(firstName, lastname)
    }

    private fun printFilteredNames(names: List<String>, filter: (String) -> Boolean) {
        names.filter(filter).forEach {
            binding.tvHigherOrder.text = "$it from Suites"
        }
    }

    private fun rollDice(
        range: IntRange,
        time: Int,
        callBack: (result: Int) -> Unit
    ) {
        for (i in 0 until time) {
            val result = range.random()
            callBack(result)
        }
    }

    private fun rollDick(callBack: ((result: Int) -> Unit)? = null) {
        println("Rolling dice started")
        thread {
            Thread.sleep(5000)
            callBack?.invoke((1..6).random())
        }
        println("Rolling dice ended")
    }

    /**
     * inline function (will insert entire function body whenever function get used)*/
    private fun guide() {
        println("guide start")
        //teach()
        teachHigherOrder({
            println("from inline callBack")
        }, {
            println("from another callBack noninline")
        })
        println("guide end")
    }

    private inline fun teach() {
        println("teach")
    }

    /**
     * noinline usage
     */
    private inline fun teachHigherOrder(
        callBack: () -> Unit,
        noinline anotherCallBack: () -> Unit
    ) {
        callBack()
        anotherCallBack()
    }

    /**
     * crossinline usage
     */
    private fun crossInLineGuild() {
        techHigherOrderCrossInline {
            println("inline")
            //return /*return is not allowed here becuase of crossinline keyword in inline function*/
        }
    }

    private inline fun techHigherOrderCrossInline(crossinline crossInLineCallBack: () -> Unit) {
        crossInLineCallBack()
    }

    /**
     * coroutine sample for switching between thread - asynchronous function call
     */
    private fun fetchAndShowUser() {
        lifecycleScope.launch(Dispatchers.Main + coroutineExceptionHandler()) {
            fetchUser()
            showUser()
        }
    }

    private suspend fun fetchUser() {
        // network call for fetching user data
        return withContext(Dispatchers.IO + coroutineExceptionHandler()) {

        }
    }

    private fun showUser() {
        // display user data
    }

    /**
     * coroutine exception handler
     */
    private fun coroutineExceptionHandler(): CoroutineExceptionHandler {
        return CoroutineExceptionHandler { _, exception ->
            println("$exception handled !")
        }
    }

    /**
     * long running task using flow zip operator
     */
    private suspend fun flowLongRunningTask() {
        /*val flowA = flowOf(1, 2, 3)
        val flowB = flowOf("A", "B", "C")

        flowA.zip(flowB) { intValue, stringValue ->
            "$intValue$stringValue"
        }.collect { combinedValue ->
            println(combinedValue)
        }*/

        startLongRunningTask()
    }

    private fun flowA(): Flow<String> {
        return flow {
            delay(5000)
            emit("One")
        }
    }

    private fun flowB(): Flow<String> {
        return flow {
            delay(5000)
            emit("Two")
        }
    }

    private fun startLongRunningTask() {
        lifecycleScope.launch {
            flowA().zip(flowB()) { valueFlowA, valueFlowB ->
                return@zip valueFlowA + valueFlowB
            }.catch { e ->
                Log.e("Error", e.localizedMessage.toString())
            }.collect {
                println(it)
            }
        }
    }
}