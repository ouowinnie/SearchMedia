package com.example.imagesearch

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

class StorageFragment : Fragment() {
    private lateinit var binding: FragmentStorageBinding
    private lateinit var adapter: RvAdapter
    private val rvModelList = mutableListOf<RvModel>()
    private lateinit var viewModel: SharedViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStorageBinding.inflate(layoutInflater)

        adapter = RvAdapter(rvModelList)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)

        viewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        viewModel.selectedRvItem.observe(viewLifecycleOwner) { selectedItem ->
            selectedItem?.let {
                rvModelList.clear()
                rvModelList.addAll(selectedItem)
                adapter.notifyDataSetChanged()
            }
        }

        adapter.itemClick = object : RvAdapter.ItemClick {
            override fun onClick(view: View, position: Int) {
                val clickedItem = rvModelList[position]
                Log.d("아이템 클릭", "position: $position")

                val selectedItems = viewModel.selectedRvItem.value?.toMutableList() ?: mutableListOf()
                selectedItems.remove(clickedItem)
                viewModel.selectedRvItem.value = selectedItems
                //(activity as MainActivity).setFragment(StorageFragment())
                Toast.makeText(context, "지울라구!!!!", Toast.LENGTH_SHORT).show()
            }
        }

        val itemSpacingDecoration = ItemSpacingDecoration(30)
        binding.recyclerView.addItemDecoration(itemSpacingDecoration)

        return binding.root
    }
}