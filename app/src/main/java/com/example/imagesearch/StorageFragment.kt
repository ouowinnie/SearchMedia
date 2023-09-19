package com.example.imagesearch

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.imagesearch.databinding.FragmentStorageBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class StorageFragment : Fragment() {
    private lateinit var binding: FragmentStorageBinding
    private lateinit var adapter: RvAdapter
    private val rvModelList = mutableListOf<RvModel>()
    private lateinit var viewModel: SharedViewModel

    private lateinit var prefs : SharedPreferences
    private lateinit var editPrefs: SharedPreferences.Editor
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStorageBinding.inflate(layoutInflater)

        adapter = RvAdapter(rvModelList)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)

        prefs = requireContext().getSharedPreferences("pref_file", Context.MODE_PRIVATE)
        editPrefs = prefs.edit()

        viewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        viewModel.selectedRvItem.observe(viewLifecycleOwner) { selectedItem ->
            selectedItem?.let {
                rvModelList.clear()
                rvModelList.addAll(selectedItem.filter { it.isLiked && it.storageFragment == "StorageFragment" })
                adapter.notifyDataSetChanged()
            }
        }
        // 선택한 아이템 삭제
        adapter.itemClick = object : RvAdapter.ItemClick {
            override fun onClick(view: View, position: Int) {
                val clickedItem = rvModelList[position]
                val selectedItems = viewModel.selectedRvItem.value?.toMutableList() ?: mutableListOf()
                selectedItems.remove(clickedItem)
                viewModel.selectedRvItem.value = selectedItems
                adapter.notifyDataSetChanged()
            }
        }

        val itemSpacingDecoration = ItemSpacingDecoration(30)
        binding.recyclerView.addItemDecoration(itemSpacingDecoration)

        loadLastSelectedItem()

        return binding.root
    }

    private fun loadLastSelectedItem() {
        val gson = Gson()
        val itemJson = prefs.getString("lastSelectedItem", null)

        if (itemJson != null) {
            val lastSelectedItems = gson.fromJson<List<RvModel>>(
                itemJson,
                object : TypeToken<List<RvModel>>() {}.type
            )
            rvModelList.clear()
            rvModelList.addAll(lastSelectedItems)
            adapter.notifyDataSetChanged()
        }
    }
}