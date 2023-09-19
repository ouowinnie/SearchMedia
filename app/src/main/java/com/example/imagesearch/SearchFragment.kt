package com.example.imagesearch

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.imagesearch.databinding.FragmentSearchBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var adapter: RvAdapter
    private lateinit var viewModel: SharedViewModel
    private val rvModelList  = mutableListOf<RvModel>()

    private lateinit var prefs : SharedPreferences
    private lateinit var editPrefs: SharedPreferences.Editor

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 뷰 바인딩, 리사이클러뷰, 뷰 모델, 쉐어드 프리퍼런스 초기화
        binding = FragmentSearchBinding.inflate(layoutInflater)
        setupRecyclerView()
        viewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        prefs = requireContext().getSharedPreferences("pref_file", Context.MODE_PRIVATE)
        editPrefs = prefs.edit()

        // 검색 버튼 설정, 이전 검색어 불러오기
        setupSearchButton()
        loadLastSearchQuery()

        return binding.root
    }

    // RecyclerView 설정
    // 리사이클러뷰 아이템 클릭 시 현재 위치 값을 넘겨준다
    private fun setupRecyclerView() {
        adapter = RvAdapter(rvModelList)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        binding.recyclerView.addItemDecoration(ItemSpacingDecoration(30))

        adapter.itemClick = object : RvAdapter.ItemClick {
            override fun onClick(view: View, position: Int) {
                handleItemClick(position)
            }
        }
    }

    // 검색 버튼 클릭 시 검색어가 있으면 검색 수행
    private fun setupSearchButton() {
        binding.searchButton.setOnClickListener {
            val query = binding.searchEditText.text.toString()
            if (query.isNotEmpty()) {
                performSearch(query)
                Log.d("버튼", "넘겨주고 있는겨 ~")
            }
        }
    }

    // 이전(마지막) 검색어를 검색창에 설정해 화면에 남겨놓기
    private fun loadLastSearchQuery() {
        val lastSearchQuery = prefs.getString("searchQuery", "") ?: ""
        if (lastSearchQuery.isNotEmpty()) {
            binding.searchEditText.setText(lastSearchQuery)
        }
    }

    private fun performSearch(query: String) {
        // 검색 API 호출
        val imageCall = viewModel.searchImages(query)
        val videoCall = viewModel.searchVideos(query)
        // 검색 결과 저장 리스트
        val imageResults: MutableList<RvModel> = mutableListOf()
        val videoResults: MutableList<RvModel> = mutableListOf()

        fun handleSearchResponse(resultList: MutableList<RvModel>) = object : Callback<RvModelList> {
            override fun onResponse(call: Call<RvModelList>, response: Response<RvModelList>) {
                if (response.isSuccessful) {
                    response.body()?.data?.let {
                        // 검색 결과를 저장 리스트에 추가하고 RecyclerView 업데이트
                        resultList.addAll(it)
                        adapter.notifyDataSetChanged()
                        combineResults(imageResults, videoResults)
                    }
                }
            }
            // 실패 시 처리
            override fun onFailure(call: Call<RvModelList>, t: Throwable) {
            }
        }

        // 이미지, 비디오 검색 결과 콜백 저장
        // handleSearchResponse() : Retrofit 콜백 객체 생성, 검색 결과를 처리하고 UI에 표시하기 위한 로직
        // imageCallback, videoCallback : 이미지와 비디오 검색에 대한 콜백 객체
        val imageCallback = handleSearchResponse(imageResults)
        val videoCallback = handleSearchResponse(videoResults)

        // API 호출
        // imageCallback, videoCallback : 서버에 이미지, 비디오 검색 요청하는 객체
        // enqueue : 요청을 비동기적으로 실행하고, 서버 응답이 도착하면 해당 응답을 처리하기 위해
        // 미리 정의한 콜백 함수 (imageCallback 및 videoCallback)를 호출
        imageCall.enqueue(imageCallback)
        videoCall.enqueue(videoCallback)

        saveSearchQuery(query)
    }

    // 검색어 저장
    private fun saveSearchQuery(query: String) {
        editPrefs.putString("searchQuery", query).apply()
    }
    // 검색 아이템 저장, 데이터 넘겨주기
    private fun saveLastSelectedItem(items: List<RvModel>) {
        val gson = Gson()
        val itemJson = gson.toJson(items, object : TypeToken<List<RvModel>>() {}.type)
        editPrefs.putString("lastSelectedItem", itemJson)
        editPrefs.apply()
    }
    // 아이템 클릭 처리
    private fun handleItemClick(position: Int) {
        val clickedItem = rvModelList[position]
        // 선택된 아이템을 뷰 모델에 추가
        val selectedItems = viewModel.selectedRvItem.value?.toMutableList() ?: mutableListOf()
        selectedItems.add(clickedItem)
        saveLastSelectedItem(selectedItems)
        viewModel.selectedRvItem.value = selectedItems
        Toast.makeText(context, "보관함에 추가 되었습니다.", Toast.LENGTH_SHORT).show()
    }
    // 검색 결과 이미지, 비디오를 합쳐서 RecyclerView에 표시
    private fun combineResults(imageResults: List<RvModel>, videoResults: List<RvModel>) {
        rvModelList.apply {
            clear()
            addAll(imageResults)
            addAll(videoResults)
            sortByDescending { it.datetime }
        }
        adapter.notifyDataSetChanged()
    }
}
