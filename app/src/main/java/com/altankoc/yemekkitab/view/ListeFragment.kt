package com.altankoc.yemekkitab.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.altankoc.yemekkitab.adapter.YemekAdapter
import com.altankoc.yemekkitab.databinding.FragmentListeBinding
import com.altankoc.yemekkitab.model.Yemek
import com.altankoc.yemekkitab.roomdb.YemekDao
import com.altankoc.yemekkitab.roomdb.YemekDatabase
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers


class ListeFragment : Fragment() {
    private var _binding: FragmentListeBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: YemekDatabase
    private lateinit var yemekDao: YemekDao
    private val mDisposable = CompositeDisposable()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = Room.databaseBuilder(requireContext(),YemekDatabase::class.java,"Yemekler").build()
        yemekDao = db.yemekDao()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentListeBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.floatingActionButton.setOnClickListener { yeniYemekEkle(it) }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        veriCek()
    }

    private fun veriCek(){
        mDisposable.add(
            yemekDao.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponse)
        )
    }

    private fun handleResponse(yemekler: List<Yemek>){

        val adapter = YemekAdapter(yemekler)
        binding.recyclerView.adapter = adapter
    }




    fun yeniYemekEkle(view: View){
        val action = ListeFragmentDirections.actionListeFragmentToTarifFragment()
        Navigation.findNavController(view).navigate(action)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mDisposable.clear()
    }

}