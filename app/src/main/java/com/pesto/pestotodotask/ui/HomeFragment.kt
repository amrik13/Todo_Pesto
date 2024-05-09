package com.pesto.pestotodotask.ui

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
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.pesto.pestotodotask.R
import com.pesto.pestotodotask.utils.TodoDBUtils
import com.pesto.pestotodotask.adapters.TaskListAdapter
import com.pesto.pestotodotask.data.TaskData
import com.pesto.pestotodotask.databinding.FragmentHomeBinding
import com.pesto.pestotodotask.utils.Constant
import com.pesto.pestotodotask.utils.PrefUtils
import com.pesto.pestotodotask.utils.TaskListClickListener
import kotlinx.coroutines.launch

class HomeFragment : Fragment(), TaskListClickListener, AdapterView.OnItemSelectedListener {

    private val TAG = HomeFragment::class.java.simpleName
    private lateinit var binding: FragmentHomeBinding
    private lateinit var itemAdapter: TaskListAdapter
    private lateinit var navController: NavController
    private var taskList: ArrayList<TaskData> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        initUI()
        return binding.root
    }

    private fun initUI() {
        navController = Navigation.findNavController(requireActivity(), R.id.nav_graph_host)
        if (!PrefUtils.isUserLoggedIn(requireContext())) {
            navController.navigate(R.id.action_home_to_login)
            Toast.makeText(requireContext(), getString(R.string.login_first), Toast.LENGTH_SHORT).show()
            return
        }
        Constant.USER_ID = PrefUtils.getUserId(requireContext()).toString()
        binding.progressBar.visibility = View.VISIBLE
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.allTaskRecyclerView.layoutManager = layoutManager
        itemAdapter = TaskListAdapter(arrayListOf())
        itemAdapter.setItemClickListener(this)
        binding.allTaskRecyclerView.adapter = itemAdapter

        binding.addTaskFab.setOnClickListener {
            gotoAddTask(null)
        }
        requireActivity().onBackPressedDispatcher.addCallback {
            requireActivity().finish()
        }

        lifecycleScope.launch {
            TodoDBUtils.getAllTasksForUser(userId = Constant.USER_ID, this@HomeFragment)
        }

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.status_filter_list,
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item).also {
            it.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item)
            binding.filterButton.adapter = it
        }
        binding.filterButton.onItemSelectedListener = this
        binding.filterButton.setSelection(0)
        filterTaskWithTitleSearch()

        binding.logout.setOnClickListener {
            PrefUtils.setUserLoggedIn(requireContext(), false)
            PrefUtils.setUserId(requireContext(), "")
            Toast.makeText(requireContext(),
                getString(R.string.logged_out_successfully), Toast.LENGTH_SHORT).show()
            navController.navigate(R.id.action_home_to_login)
        }
    }

    fun setTaskListToAdapter(taskList: ArrayList<TaskData>?) {
        taskList?.let {
            this.taskList = it
            itemAdapter.setTaskList(taskList = it)
            itemAdapter.notifyDataSetChanged()
        }
        binding.progressBar.visibility = View.GONE
    }

    private fun gotoAddTask(argument: Bundle?) {
        navController.navigate(R.id.action_home_to_addTask, argument)
    }

    override fun onTaskClickListener(taskData: TaskData) {
        val bundle = Bundle()
        bundle.putString(Constant.TASKS_ID, taskData.taskId)
        bundle.putString(Constant.TITLE, taskData.title)
        bundle.putString(Constant.DESCRIPTION, taskData.description)
        bundle.putString(Constant.STATUS, taskData.status)
        bundle.putLong(Constant.DUE_DATE, taskData.dueDate)
        gotoAddTask(bundle)
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val statusFilter = binding.filterButton.getItemAtPosition(position) as String
        filterTaskWithStatus(statusFilter)
    }

    private fun filterTaskWithStatus(statusFilter: String) {
        Log.d(TAG, statusFilter)
        if (statusFilter == Constant.ALL) {
            setTaskListToAdapter(taskList = taskList)
        } else {
            val filteredTaskList = arrayListOf<TaskData>()
            taskList.forEach {
                if (it.status == statusFilter) {
                    filteredTaskList.add(it)
                }
            }
            itemAdapter.setTaskList(taskList = filteredTaskList)
            itemAdapter.notifyDataSetChanged()
        }
    }

    private fun filterTaskWithTitleSearch() {

        binding.searchBar.addTextChangedListener {
            val searchQuery = it.toString()
            Log.d(TAG, "searchQuery: ${searchQuery}")
            if (searchQuery.length > 3) {
                lifecycleScope.launch {
                    Log.d(TAG, "filter started")
                    val filteredTaskList = arrayListOf<TaskData>()
                    taskList.forEach {
                        if (it.title.contains(searchQuery, true)) {
                            filteredTaskList.add(it)
                        }
                    }
                    itemAdapter.setTaskList(taskList = filteredTaskList)
                    itemAdapter.notifyDataSetChanged()
                }
            } else {
                setTaskListToAdapter(taskList = taskList)
            }
        }

    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}

    override fun onStart() {
        binding.filterButton.setSelection(0)
        super.onStart()
    }

}