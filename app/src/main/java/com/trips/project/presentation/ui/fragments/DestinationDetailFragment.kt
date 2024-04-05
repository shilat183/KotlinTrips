package com.trips.project.presentation.ui.fragments

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.room.Room
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.trips.project.R
import com.trips.project.data.db.DestinationDatabse
import com.trips.project.data.db.DestinationDao
import com.trips.project.data.model.DestinationModel
import com.squareup.picasso.Picasso
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.firebase.auth.FirebaseAuth
import me.ibrahimsn.lib.SmoothBottomBar

class DestinationDetailFragment : Fragment() {
    private lateinit var firestore: FirebaseFirestore
    private lateinit var myDB: DestinationDatabse
    private lateinit var destinationDao: DestinationDao
    private lateinit var model: DestinationModel

    private lateinit var name_edittext: EditText
    private lateinit var price_edittext: EditText
    private lateinit var destinationImageView1: ImageView
    private lateinit var destinationImageView2: ImageView
    private lateinit var destinationImageView3: ImageView
    private lateinit var flighttime_edittext: EditText
    private lateinit var flightcompany_edittext: EditText
    private lateinit var tripduration_edittext: EditText
    private lateinit var countryname_edittext: EditText

    private lateinit var saveButton: Button
    private lateinit var deleteButton: Button
    private var selectedImageUri1: Uri? = null
    private var selectedImageUri2: Uri? = null
    private var selectedImageUri3: Uri? = null
    private lateinit var mAuth: FirebaseAuth
    private val AUTOCOMPLETE_REQUEST_CODE = 1

    override fun onResume() {
        super.onResume()
        hideBottomNavigationBar()
    }

    override fun onPause() {
        super.onPause()
        showBottomNavigationBar()
    }

    private fun hideBottomNavigationBar() {
        val bottomNavigationView = requireActivity().findViewById<SmoothBottomBar>(R.id.bottom_navigation)
        bottomNavigationView.visibility = View.GONE
    }

    private fun showBottomNavigationBar() {
        val bottomNavigationView = requireActivity().findViewById<SmoothBottomBar>(R.id.bottom_navigation)
        bottomNavigationView.visibility = View.VISIBLE
    }
    private  var imageViewIndex: Int = 0
    val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { result ->
        if (result != null) {
            try {
                when (imageViewIndex) {
                    1 -> {
                        Picasso.get().load(result).into(destinationImageView1, object : com.squareup.picasso.Callback {
                            override fun onSuccess() {
                                selectedImageUri1 = result
                            }
                            override fun onError(e: Exception?) {
                                Toast.makeText(requireContext(), "Failed to load image", Toast.LENGTH_SHORT).show()
                                e?.printStackTrace()
                            }
                        })
                    }
                    2 -> {
                        Picasso.get().load(result).into(destinationImageView2, object : com.squareup.picasso.Callback {
                            override fun onSuccess() {
                                selectedImageUri2 = result
                            }
                            override fun onError(e: Exception?) {
                                Toast.makeText(requireContext(), "Failed to load image", Toast.LENGTH_SHORT).show()
                                e?.printStackTrace()
                            }
                        })
                    }
                    3 -> {
                        Picasso.get().load(result).into(destinationImageView3, object : com.squareup.picasso.Callback {
                            override fun onSuccess() {
                                selectedImageUri3 = result
                            }
                            override fun onError(e: Exception?) {
                                Toast.makeText(requireContext(), "Failed to load image", Toast.LENGTH_SHORT).show()
                                e?.printStackTrace()
                            }
                        })
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firestore = FirebaseFirestore.getInstance()
        myDB = Room.databaseBuilder(requireContext().applicationContext,
            DestinationDatabse::class.java, "destination_database").allowMainThreadQueries().build()
        destinationDao = myDB.destinationDao()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val callback1 = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback1)
    }
    private fun selectAddress() {
        val fields = listOf(Place.Field.ID, Place.Field.NAME)
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
            .setCountry("US")
            .build(requireContext())
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val place = Autocomplete.getPlaceFromIntent(data!!)
                val address = place.name
                countryname_edittext.setText(address)
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                val status = Autocomplete.getStatusFromIntent(data!!)
            }
        }
    }
    private fun initializeViews(rootView: View) {
        name_edittext = rootView.findViewById(R.id.name_edittext)
        price_edittext = rootView.findViewById(R.id.price_edittext)
        destinationImageView1 = rootView.findViewById(R.id.destinationImageView1)
        destinationImageView2 = rootView.findViewById(R.id.destinationImageView2)
        destinationImageView3 = rootView.findViewById(R.id.destinationImageView3)
        countryname_edittext = rootView.findViewById(R.id.countryname_edittext)
        flighttime_edittext = rootView.findViewById(R.id.flighttime_edittext)
        flightcompany_edittext = rootView.findViewById(R.id.flightcompany_edittext)
        tripduration_edittext = rootView.findViewById(R.id.tripduration_edittext)
        saveButton = rootView.findViewById(R.id.save_button)
        deleteButton = rootView.findViewById(R.id.delete_button)

    }
    private fun selectImage(index: Int) {
        imageViewIndex = index; 
        imagePickerLauncher.launch("image/*")
    }

    private fun setupListeners() {
        destinationImageView1.setOnClickListener { selectImage(1) }
        destinationImageView2.setOnClickListener { selectImage(2) }
        destinationImageView3.setOnClickListener { selectImage(3) }
        countryname_edittext.setOnClickListener { selectAddress() }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_destination_detail, container, false)
        mAuth = FirebaseAuth.getInstance()
        initializeViews(rootView)
        setupListeners()
        saveButton.setOnClickListener {
            val progressDialog = ProgressDialog(requireContext())
            progressDialog.setMessage("Updating destination...")
            progressDialog.setCancelable(false)
            progressDialog.show()
            if(selectedImageUri1 == null || selectedImageUri2 == null || selectedImageUri3 == null){
                val destination = hashMapOf<String, Any>()
                destination["name"] = name_edittext.text.toString().trim()
                destination["price"] = price_edittext.text.toString().trim()
                destination["userEmail"] = FirebaseAuth.getInstance().currentUser?.email!!
                destination["imageUrl1"] = model.imageUrl1
                destination["imageUrl2"] = model.imageUrl2
                destination["imageUrl3"] = model.imageUrl3
                destination["country_name"] = countryname_edittext.text.toString().trim()
                destination["flight_time"] = flighttime_edittext.text.toString().trim()
                destination["flight_company"] = flightcompany_edittext.text.toString().trim()
                destination["trip_duration"] = tripduration_edittext.text.toString().trim()

                firestore.collection("Destinations").document(model.key.toString())
                    .update(destination)
                    .addOnSuccessListener {
                        model.apply {
                            userEmail = FirebaseAuth.getInstance().currentUser?.email!!
                            imageUrl1 = model.imageUrl1
                            imageUrl2 = model.imageUrl2
                            imageUrl3 = model.imageUrl3
                            name = name_edittext.text.toString().trim()
                            price = price_edittext.text.toString().trim()
                            country_name = countryname_edittext.text.toString().trim()
                            flight_time = flighttime_edittext.text.toString().trim()
                            flight_company = flightcompany_edittext.text.toString().trim()
                            trip_duration = tripduration_edittext.text.toString().trim()
                        }
                        progressDialog.dismiss()
                        destinationDao.update(model)
                        Toast.makeText(context, "Destination updated successfully", Toast.LENGTH_SHORT).show()
                        NavHostFragment.findNavController(this).popBackStack()
                    }
                    .addOnFailureListener { Toast.makeText(context, "Error updating destination", Toast.LENGTH_SHORT).show() }

            }else{
                val storage = FirebaseStorage.getInstance()
                val imageRef1 = storage.reference.child("destination_images/${name_edittext.text.toString()}_1.jpg")
                val imageRef2 = storage.reference.child("destination_images/${name_edittext.text.toString()}_2.jpg")
                val imageRef3 = storage.reference.child("destination_images/${name_edittext.text.toString()}_3.jpg")

                imageRef1.putFile(selectedImageUri1!!)
                    .addOnSuccessListener { taskSnapshot ->
                        imageRef1.downloadUrl
                            .addOnSuccessListener { uri1 ->
                                imageRef2.putFile(selectedImageUri2!!)
                                    .addOnSuccessListener { taskSnapshot ->
                                        imageRef2.downloadUrl
                                            .addOnSuccessListener { uri2 ->
                                                imageRef3.putFile(selectedImageUri3!!)
                                                    .addOnSuccessListener { taskSnapshot ->
                                                        imageRef3.downloadUrl
                                                            .addOnSuccessListener { uri3 ->

                                                                val destination = hashMapOf<String, Any>()
                                                                destination["name"] = name_edittext.text.toString().trim()
                                                                destination["price"] = price_edittext.text.toString().trim()
                                                                destination["userEmail"] = FirebaseAuth.getInstance().currentUser?.email!!
                                                                destination["imageUrl1"] = uri1.toString()
                                                                destination["imageUrl2"] = uri2.toString()
                                                                destination["imageUrl3"] = uri3.toString()
                                                                destination["country_name"] = countryname_edittext.text.toString().trim()
                                                                destination["flight_time"] = flighttime_edittext.text.toString().trim()
                                                                destination["flight_company"] = flightcompany_edittext.text.toString().trim()
                                                                destination["trip_duration"] = tripduration_edittext.text.toString().trim()

                                                                firestore.collection("Destinations").document(model.key.toString())
                                                                    .update(destination)
                                                                    .addOnSuccessListener { documentReference ->
                                                                        progressDialog.dismiss()
                                                                        Toast.makeText(requireContext(), "Destination updated successfully", Toast.LENGTH_SHORT).show()
                                                                        NavHostFragment.findNavController(this).popBackStack()
                                                                        model.apply {
                                                                            userEmail = FirebaseAuth.getInstance().currentUser?.email!!
                                                                            imageUrl1 = uri1.toString()
                                                                            imageUrl2 = uri2.toString()
                                                                            imageUrl3 = uri3.toString()
                                                                            name = name_edittext.text.toString().trim()
                                                                            price = price_edittext.text.toString().trim()
                                                                            country_name = countryname_edittext.text.toString().trim()
                                                                            flight_time = flighttime_edittext.text.toString().trim()
                                                                            flight_company = flightcompany_edittext.text.toString().trim()
                                                                            trip_duration = tripduration_edittext.text.toString().trim()
                                                                        }
                                                                        Thread { destinationDao.update(model) }.start()
                                                                        NavHostFragment.findNavController(this).popBackStack()
                                                                    }
                                                                    .addOnFailureListener { e ->
                                                                        progressDialog.dismiss()
                                                                        Toast.makeText(requireContext(), "Failed to update destination", Toast.LENGTH_SHORT).show()
                                                                    }
                                                            }
                                                    }
                                            }
                                    }
                            }
                    }
                    .addOnFailureListener { e ->
                        progressDialog.dismiss()
                        Toast.makeText(requireContext(), "Failed to upload image", Toast.LENGTH_SHORT).show()
                    }
            }

        }

        deleteButton.setOnClickListener {
            model.key?.let { destinationKey ->
                firestore.collection("Destinations").document(destinationKey)
                    .delete()
                    .addOnSuccessListener {
                        destinationDao.delete(model)
                        Toast.makeText(context, "Destination deleted successfully", Toast.LENGTH_SHORT).show()
                        activity?.onBackPressed()
                    }
                    .addOnFailureListener { Toast.makeText(context, "Error deleting destination", Toast.LENGTH_SHORT).show() }
            }
        }

        val bundle = arguments
        bundle?.let {
            setCurrentDestination(bundle = it)

            name_edittext.setText(model.name)
            price_edittext.setText(model.price)
            countryname_edittext.setText(model.country_name)
            flighttime_edittext.setText(model.flight_time)
            flightcompany_edittext.setText(model.flight_company)
            tripduration_edittext.setText(model.trip_duration)
            Picasso.get().load(model.imageUrl1).into(destinationImageView1)
            Picasso.get().load(model.imageUrl2).into(destinationImageView2)
            Picasso.get().load(model.imageUrl3).into(destinationImageView3)
        }


        return rootView
    }


    private fun setCurrentDestination(bundle: Bundle) {

        model = DestinationModel(
            key = bundle.getString("destinationId")?: "",
            userEmail = FirebaseAuth.getInstance().currentUser?.email!!,
            imageUrl1 = bundle.getString("imageUrl1")?: "",
            imageUrl2 = bundle.getString("imageUrl2")?: "",
            imageUrl3 = bundle.getString("imageUrl3")?: "",
            name = bundle.getString("name")?: "",
            price = bundle.getString("price")?: "",
            country_name = bundle.getString("country_name")?: "",
            flight_time = bundle.getString("flight_time")?: "",
            flight_company = bundle.getString("flight_company")?: "",
            trip_duration = bundle.getString("trip_duration")?: "",
        )

        val isEdit = bundle.getBoolean("isEdit")
        if (!isEdit) {
            name_edittext.isEnabled = false
            price_edittext.isEnabled = false
            countryname_edittext.isEnabled = false
            flighttime_edittext.isEnabled = false
            flightcompany_edittext.isEnabled = false
            tripduration_edittext.isEnabled = false
            destinationImageView1.isEnabled = false
            destinationImageView2.isEnabled = false
            destinationImageView3.isEnabled = false
            deleteButton.visibility = View.GONE
            saveButton.visibility = View.GONE
        }
    }


}
