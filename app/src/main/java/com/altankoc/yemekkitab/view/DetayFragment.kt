package com.altankoc.yemekkitab.view

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.room.Room
import com.altankoc.yemekkitab.R
import com.altankoc.yemekkitab.databinding.FragmentDetayBinding
import com.altankoc.yemekkitab.model.Yemek
import com.altankoc.yemekkitab.roomdb.YemekDao
import com.altankoc.yemekkitab.roomdb.YemekDatabase
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers


class DetayFragment : Fragment() {
    private var _binding: FragmentDetayBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: YemekDatabase
    private lateinit var yemekDao: YemekDao
    private val mDisposable = CompositeDisposable()
    private var secilenYemek: Yemek? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = Room.databaseBuilder(requireContext(),YemekDatabase::class.java,"Yemekler").build()
        yemekDao = db.yemekDao()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetayBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonDetaySil.setOnClickListener { yemekSil(it) }
        binding.buttonBack.setOnClickListener { geriGit(it) }

        arguments?.let {
            val gelenBilgi = DetayFragmentArgs.fromBundle(it).bilgi

            val id = DetayFragmentArgs.fromBundle(it).bilgi

            mDisposable.add(
                yemekDao.findById(id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::handleResponse)
            )

        }

    }

    private fun handleResponse(yemek: Yemek){
        binding.yemekBaslik.text = yemek.isim
        binding.detayTarif.text = yemek.tarif
        binding.detayMalzemeler.text = yemek.malzeme
        val bitmap = BitmapFactory.decodeByteArray(yemek.gorsel,0,yemek.gorsel.size)
        binding.yemekDetayResim.setImageBitmap(bitmap)
        secilenYemek = yemek
    }

    fun yemekSil(view: View){

       if(secilenYemek != null){
           mDisposable.add(
               yemekDao.delete(yemek = secilenYemek!!)
                   .subscribeOn(Schedulers.io())
                   .observeOn(AndroidSchedulers.mainThread())
                   .subscribe(this::handleResponseForDelete)
           )
       }
    }

    fun geriGit(view: View){
        val navController = Navigation.findNavController(requireView())

        navController.popBackStack()

        navController.navigate(R.id.listeFragment)
    }

    fun handleResponseForDelete(){
        val navController = Navigation.findNavController(requireView())

        navController.popBackStack()

        navController.navigate(R.id.listeFragment)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mDisposable.clear()

        requireActivity().supportFragmentManager.popBackStack()

    }



}