package com.pesto.pestotodotask.ui

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.addCallback
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.pesto.pestotodotask.R
import com.pesto.pestotodotask.utils.TodoDBUtils
import com.pesto.pestotodotask.databinding.FragmentAddTaskBinding
import com.pesto.pestotodotask.utils.Constant
import kotlinx.coroutines.launch
import java.util.UUID

class AddTaskFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private val TAG = AddTaskFragment::class.java.simpleName
    private lateinit var binding: FragmentAddTaskBinding
    private lateinit var navController: NavController
    private var mCurrentStatus: String? = null

    private var taskId = ""
    private var title = ""
    private var description = ""
    private var status = ""
    private var dueDate = 0L
    private var isUpdate = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddTaskBinding.inflate(inflater, container, false)
        getBundleData()
        initUI()
        return binding.root
    }

    private fun getBundleData() {
        if (arguments == null || arguments?.isEmpty == true) return

        title = requireArguments().getString(Constant.TITLE) ?: ""
        description = requireArguments().getString(Constant.DESCRIPTION) ?: ""
        status = requireArguments().getString(Constant.STATUS) ?: ""
        dueDate = requireArguments().getLong(Constant.DUE_DATE)
        taskId = requireArguments().getString(Constant.TASKS_ID) ?: ""
        isUpdate = true
    }

    private fun initUI() {
        if (isUpdate) {
            binding.addTaskButton.text = getString(R.string.update_button)
            binding.deleteTaskButton.visibility = View.VISIBLE
        }
        navController = Navigation.findNavController(requireActivity(), R.id.nav_graph_host)
        requireActivity().onBackPressedDispatcher.addCallback {
            navController.popBackStack()
        }

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.status_list,
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item).also {
                it.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item)
                binding.statusSpinner.adapter = it
        }
        binding.statusSpinner.onItemSelectedListener = this

        binding.apply {

            setBundleDataFromHome()

            addTaskButton.setOnClickListener {
                val title = titleInput.text ?: ""
                val description = descriptionInput.text ?: ""
                val status = mCurrentStatus ?: ""
                Log.d(TAG, "title: $title, desc: $description, status: $status")
                if (title.isEmpty() || description.isEmpty() || status.isEmpty()) {
                    Toast.makeText(requireContext(),
                        getString(R.string.add_task_toast_msg), Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                lifecycleScope.launch {
                    val taskId =
                        if (this@AddTaskFragment.taskId.isEmpty()) UUID.randomUUID()
                        else this@AddTaskFragment.taskId
                    TodoDBUtils.setTasksForUser(
                        Constant.USER_ID,
                        taskId = taskId.toString(),
                        title = title.toString(),
                        description = description.toString(),
                        status = status,
                        dueDate = 0
                    )
                    if (!isUpdate) {
                        clearAll()
                        Toast.makeText(requireContext(),
                            getString(R.string.task_added_successfully), Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(),
                            getString(R.string.updated_successfully), Toast.LENGTH_SHORT).show()
                    }
                }

            }

            deleteTaskButton.setOnClickListener {
                AlertDialog.Builder(requireActivity())
                    .setTitle(getString(R.string.are_you_sure))
                    .setMessage(getString(R.string.this_will_permanently_delete_the_task))
                    .setCancelable(false)
                    .setPositiveButton(R.string.delete) { dialog, which ->
                        TodoDBUtils.deleteTask(Constant.USER_ID, taskId = taskId)
                        navController.popBackStack()
                    }.setNegativeButton(getString(R.string.cancel)) { dialog, which ->
                        dialog.dismiss()
                    }.show()
            }

        }
    }

    private fun clearAll() {
        binding.apply {
            titleInput.text?.clear()
            descriptionInput.text?.clear()
            statusSpinner.setSelection(0)
        }
        title = ""
        description = ""
        status = ""
        taskId = ""

    }

    private fun setBundleDataFromHome() {
        binding.apply {
            if (title.isNotEmpty()) titleInput.setText(title)
            if (description.isNotEmpty()) descriptionInput.setText(description)
            if (status.isNotEmpty()) {
                for (i in 0 until statusSpinner.adapter.count) {
                    if (status == statusSpinner.getItemAtPosition(i)) {
                        statusSpinner.setSelection(i)
                        mCurrentStatus = status
                        break
                    }
                }
            }
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        mCurrentStatus = binding.statusSpinner.getItemAtPosition(position) as String
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}

}