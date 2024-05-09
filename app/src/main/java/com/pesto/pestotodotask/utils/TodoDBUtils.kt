package com.pesto.pestotodotask.utils

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import com.pesto.pestotodotask.data.TaskData
import com.pesto.pestotodotask.ui.HomeFragment
import com.pesto.pestotodotask.ui.LoginFragment

object TodoDBUtils {
    private val TAG = TodoDBUtils::class.java.simpleName
    //USER DB created and taking reference
    private const val USER_DB_NAME = "USER"
    private val firebaseDataBase: DatabaseReference = Firebase.database.getReference(USER_DB_NAME)

    //User Fields
    private const val USER_ID_FIELD = "USERID"
    private const val NAME_FIELD = "NAME"
    private const val EMAIL_FIELD = "EMAIL"
    private const val PASSWORD_FIELD = "PASSWORD"
    private const val TASKS_FIELD = "TASKS"
    private const val LOGIN_FIELD = "IS_LOGIN"

    //Tasks Fields
    private const val TASKS_ID = "TASK_ID"
    private const val TITLE = "TITLE"
    private const val DESCRIPTION = "DESCRIPTION"
    private const val STATUS = "STATUS"
    private const val DUE_DATE = "DUE_DATE"

    // Set the user details
    fun setUser(
        userId: String,
        userName: String,
        email: String,
        password: String
    ) {
        val userRef = firebaseDataBase.child(userId)
        userRef.child(USER_ID_FIELD).setValue(userId)
        userRef.child(NAME_FIELD).setValue(userName)
        userRef.child(EMAIL_FIELD).setValue(email)
        userRef.child(LOGIN_FIELD).setValue(false)
        userRef.child(PASSWORD_FIELD).setValue(password)
    }

    fun setTasksForUser(
        userId: String,
        taskId: String,
        title: String,
        description: String,
        status: String,
        dueDate: Long
    ) {
        val taskRef = firebaseDataBase.child(userId).child(TASKS_FIELD).child(taskId)
        taskRef.child(TASKS_ID).setValue(taskId)
        taskRef.child(TITLE).setValue(title)
        taskRef.child(DESCRIPTION).setValue(description)
        taskRef.child(STATUS).setValue(status)
        taskRef.child(DUE_DATE).setValue(dueDate)
    }

    // Set the user details
    fun checkLogin(
        email: String,
        password: String,
        loginFragment: LoginFragment
    ) {
        val userRef = firebaseDataBase.child(email)
        userRef.child(PASSWORD_FIELD).get().addOnSuccessListener {
            if (password == it.value) {
                loginFragment.observeLoginCheck(true, email)
            } else {
                loginFragment.observeLoginCheck(false, email)
            }
        }.addOnFailureListener {
            loginFragment.observeLoginCheck(false, email)
        }
    }

    fun deleteTask(
        userId: String,
        taskId: String
    ) {
        firebaseDataBase.child(userId).child(TASKS_FIELD).child(taskId).removeValue()
    }

    fun getAllTasksForUser(userId: String, home: HomeFragment) {
        val taskList = arrayListOf<TaskData>()
        firebaseDataBase.child(userId).child(TASKS_FIELD).get().addOnSuccessListener {
            it.children.forEach {itt ->
                val taskId = itt.child(TASKS_ID).value.toString()
                val title = itt.child(TITLE).value.toString()
                val description = itt.child(DESCRIPTION).value.toString()
                val status = itt.child(STATUS).value.toString()
                val dueDate = itt.child(DUE_DATE).value as Long

                Log.d(TAG, itt.toString())

                taskList.add(
                    TaskData(
                        taskId = taskId,
                        title = title,
                        description = description,
                        status = status,
                        dueDate = dueDate
                    )
                )

            }
            home.setTaskListToAdapter(taskList = taskList)
        }.addOnFailureListener {
            Log.e(TAG, it.message.toString())
            home.setTaskListToAdapter(taskList = null)
        }
    }

}