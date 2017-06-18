package com.lynas.entertainmenttracker2.service

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

/**
 * Created by lynas
 * on 5/27/2017..
 */

object FireBaseService {
    fun dbRef(): DatabaseReference? {
        val firebaseAuth = FirebaseAuth.getInstance()
        val currentUserId = firebaseAuth.currentUser?.uid ?: "UnAuthenticated"
        val dbRef = FirebaseDatabase.getInstance()
        return dbRef.getReference(currentUserId)
    }

    fun deleteNodeBy(rootName: String, id: String) {
        dbRef()?.child(rootName)?.child(id)?.removeValue()
    }

    fun createOrUpdateNode(rootName: String, id: String, value: Any) {
        dbRef()?.child(rootName)?.child(id)?.setValue(value)
    }
}