package com.altankoc.yemekkitab.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.room.Room
import com.altankoc.yemekkitab.R
import com.altankoc.yemekkitab.databinding.FragmentTarifBinding
import com.altankoc.yemekkitab.model.Yemek
import com.altankoc.yemekkitab.roomdb.YemekDao
import com.altankoc.yemekkitab.roomdb.YemekDatabase
import com.google.android.material.snackbar.Snackbar
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.ByteArrayOutputStream


class TarifFragment : Fragment() {
    private var _binding: FragmentTarifBinding? = null
    private val binding get() = _binding!!
    private lateinit var permissionLaunher: ActivityResultLauncher<String>
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private var secilenGorsel: Uri? = null
    private var secilenBitmap: Bitmap? = null
    private val mDisposable = CompositeDisposable()

    private lateinit var db: YemekDatabase
    private lateinit var yemekDao: YemekDao


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerLauncher()

        db = Room.databaseBuilder(requireContext(),YemekDatabase::class.java,"Yemekler")
            .allowMainThreadQueries()
            .build()
        yemekDao = db.yemekDao()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTarifBinding.inflate(inflater, container, false)
        val view = binding.root
        return view    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.yemekResim.setOnClickListener { yemekResmiEkle(it) }
        binding.buttonKaydet.setOnClickListener { yemekKaydet(it) }


    }

    fun yemekResmiEkle(view: View){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            if(ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED){
                //izin verilmeme durumu
                if(ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),Manifest.permission.READ_MEDIA_IMAGES)){
                    //snackbar
                    Snackbar.make(view,"Yemek görseli için galeri izni gerekli!",Snackbar.LENGTH_INDEFINITE)
                        .setAction("İzin ver",View.OnClickListener {
                            //Tekrar izin isteyeğiz
                            permissionLaunher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                        }).show()
                } else{
                    //izin isteyeğiz
                    permissionLaunher.launch(Manifest.permission.READ_MEDIA_IMAGES)

                }
            } else{
                //izin verilmiş galeriye git
                val intentToGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }
        }

        else {
            if(ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                //izin verilmeme durumu
                if(ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),Manifest.permission.READ_EXTERNAL_STORAGE)){
                    //snackbar
                    Snackbar.make(view,"Yemek görseli için galeri izni gerekli!",Snackbar.LENGTH_INDEFINITE)
                        .setAction("İzin ver",View.OnClickListener {
                            //Tekrar izin isteyeğiz
                            permissionLaunher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                        }).show()
                } else{
                    //izin isteyeğiz
                    permissionLaunher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)

                }
            } else{
                //izin verilmiş galeriye git
                val intentToGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }
        }

    }


    fun yemekKaydet(view: View) {
        val isim = binding.isimText.text.toString().trim()
        val malzeme = binding.malzemeText.text.toString().trim()
        val tarif = binding.tarifText.text.toString().trim()

        if (isim.isEmpty() || malzeme.isEmpty() || tarif.isEmpty() || secilenBitmap == null ) {
            Toast.makeText(requireContext(), "Lütfen tüm alanları doldurun!", Toast.LENGTH_SHORT).show()
            return
        }

        val kucukBitmap = kucukBitmapOlustur(secilenBitmap!!, 300)
        val outPutStream = ByteArrayOutputStream()
        kucukBitmap.compress(Bitmap.CompressFormat.PNG, 50, outPutStream)
        val byteDizi = outPutStream.toByteArray()

        val yemek = Yemek(isim, malzeme, tarif, byteDizi)



        //RxJava

        mDisposable.add(
            yemekDao.insert(yemek)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::handleResponseForInsert))


    }

    private fun handleResponseForInsert(){

//        val action = TarifFragmentDirections.actionTarifFragmentToListeFragment()
//        Navigation.findNavController(requireView()).navigate(action)

        val navController = Navigation.findNavController(requireView())


        navController.popBackStack()
        navController.navigate(R.id.listeFragment)

    }

    private fun registerLauncher(){

        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->

            if(result.resultCode == AppCompatActivity.RESULT_OK){
                val intentFromResult = result.data
                if(intentFromResult != null){
                    val secilenGorsel = intentFromResult.data

                   try {
                       if(Build.VERSION.SDK_INT >= 28){
                           val source = ImageDecoder.createSource(requireActivity().contentResolver,secilenGorsel!!)
                           secilenBitmap = ImageDecoder.decodeBitmap(source)
                           binding.yemekResim.setImageBitmap(secilenBitmap)
                       } else{
                           secilenBitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver,secilenGorsel)
                           binding.yemekResim.setImageBitmap(secilenBitmap)
                       }
                   }catch (e:Exception){
                       e.printStackTrace()
                   }


                }
            }

        }

        permissionLaunher = registerForActivityResult(ActivityResultContracts.RequestPermission()){ result ->
            if(result){
                //izin verildi galeriye gidebiliriz
                val intentToGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            } else{
                Toast.makeText(requireContext(),"İzin verilmedi",Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun kucukBitmapOlustur(secilenBitmap: Bitmap, maxSize:Int): Bitmap{

        var width = secilenBitmap.width
        var height = secilenBitmap.height

        val oran: Double = width.toDouble() / height.toDouble()

        if(oran>1){ //yatay
            width = maxSize
            val kisaYukseklik = width / oran
            height = kisaYukseklik.toInt()
        } else{//dikey

            height = maxSize
            val kisaGenislik = height * oran
            width = kisaGenislik.toInt()
        }

        return Bitmap.createScaledBitmap(secilenBitmap,width,height,true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mDisposable.clear()

        requireActivity().supportFragmentManager.popBackStack()

    }

}