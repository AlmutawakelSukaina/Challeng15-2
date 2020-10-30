package com.example.crimeintent1

import androidx.lifecycle.ViewModel

class CrimeListViewModel : ViewModel() {


    private val crimeRepository = CrimeRepository.get()

    val crimeListLiveData = crimeRepository.getCrimes()

    fun addCrime(crime: Crime) {
        crimeRepository.addCrime(crime)
    }
}