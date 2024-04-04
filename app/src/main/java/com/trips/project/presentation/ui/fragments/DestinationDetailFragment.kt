package com.trips.project.presentation.ui.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
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
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.firebase.auth.FirebaseAuth
import me.ibrahimsn.lib.SmoothBottomBar

class DestinationDetailFragment : Fragment(), OnMapReadyCallback  {
    private lateinit var map: GoogleMap
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var myDB: DestinationDatabse
    private lateinit var destinationDao: DestinationDao
    private lateinit var model: DestinationModel
    private lateinit var imageView: ImageView
    private lateinit var destinationNameEditText: EditText
    private lateinit var favoriteButton: ImageButton
    private lateinit var textMenu: EditText
    private lateinit var foodType: EditText
    private lateinit var map_edittext: EditText
    private lateinit var reviewEditText: EditText
    private lateinit var reviewTextView: TextView
    private lateinit var saveButton: Button
    private lateinit var deleteButton: Button
    private var selectedImageUri: Uri? = null
    private var selectedLocation: LatLng? = null
    private lateinit var mAuth: FirebaseAuth
    private var isFavorite: Boolean = false

    private val callback = OnMapReadyCallback { googleMap ->
        map = googleMap
        val bundle = arguments
        bundle?.let {
            setCurrentDestination(bundle)

            if (isFavorite) {
                favoriteButton.setImageResource(R.drawable.heart)
            } else {
                favoriteButton.setImageResource(R.drawable.heart_empty)
            }
            destinationNameEditText.setText(model.name)
            reviewTextView.setText(model.review)

            var userName = "";
            val user = mAuth.currentUser
            user?.let {
                userName = user.displayName.toString()
            }
            val reviews = model.review!!.split("\n")
            for (review in reviews) {
                if (review.startsWith("$userName:")) {
                    reviewEditText.setText(review.split(':')[1])
                }
            }

            foodType.setText(model.foodtype)
            map_edittext.setText(model.address)
            textMenu.setText(model.menu)
            Picasso.get().load(model.imageUrl).into(imageView)
            val hotelPosition = LatLng(model.latitude, model.longitude)
            val marker = map.addMarker(MarkerOptions().position(hotelPosition).title(model.name).snippet(model.key))
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(hotelPosition, 15f))
        }
    }

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { result ->
        result?.let {
            try {
                Picasso.get().load(it).into(imageView)
                selectedImageUri = it
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseFirestore = FirebaseFirestore.getInstance()
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

        val mapFragment = childFragmentManager.findFragmentById(R.id.mapsFragment) as? SupportMapFragment
        mapFragment?.getMapAsync(callback)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback1)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_destination_detail, container, false)
        mAuth = FirebaseAuth.getInstance()
        destinationNameEditText = rootView.findViewById(R.id.name_edittext)
        textMenu = rootView.findViewById(R.id.description_edittext)
        foodType = rootView.findViewById(R.id.date_edittext)
        map_edittext = rootView.findViewById(R.id.map_edittext)
        reviewEditText = rootView.findViewById(R.id.review_edittext)
        reviewTextView = rootView.findViewById(R.id.review_label)
        saveButton = rootView.findViewById(R.id.save_button)
        deleteButton = rootView.findViewById(R.id.delete_button)
        imageView = rootView.findViewById(R.id.destinationImage)

        imageView.setOnClickListener { openGallery() }
        favoriteButton = rootView.findViewById(R.id.favorite_button)
        favoriteButton.setOnClickListener {
            isFavorite = !isFavorite
            val favoriteButton: ImageButton = rootView.findViewById(R.id.favorite_button)
            if (isFavorite) {
                favoriteButton.setImageResource(R.drawable.heart)
            } else {
                favoriteButton.setImageResource(R.drawable.heart_empty)
            }
            firebaseFirestore.collection("Destinations").document(model.key.toString())
                .update("favourite", isFavorite)

                .addOnSuccessListener {
                    model.apply {
                        favourite = isFavorite
                    }
                    destinationDao.update(model)
                    Toast.makeText(context, "Destination updated successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { Toast.makeText(context, "Error updating destination", Toast.LENGTH_SHORT).show() }

        }

        saveButton.setOnClickListener {
            val newDestinationName = destinationNameEditText.text.toString()
            val newMenu = textMenu.text.toString()
            val newFoodType = foodType.text.toString()
            val newAddress = map_edittext.text.toString()
            val user = mAuth.currentUser
            var userName = "";
            user?.let {
                userName = user.displayName.toString()
            }
            val regex = Regex("$userName:.*")
            val existingText = reviewTextView.text.toString()
            var newReview  = reviewEditText.text.toString()
            if (newReview.isEmpty()){
                newReview = existingText
            }else{
                newReview = if (existingText.contains(regex)) {
                    existingText.replace(regex, "$userName: $newReview")
                } else {
                    "$existingText\n$userName: $newReview"
                }
            }
            if(selectedImageUri == null){
                val latitude1 = if (selectedLocation != null) selectedLocation!!.latitude else model.latitude
                val longitude1 = if (selectedLocation != null) selectedLocation!!.longitude else model.longitude

                firebaseFirestore.collection("Destinations").document(model.key.toString())
                    .update(
                        "name", newDestinationName,
                        "menu", newMenu,
                        "imageUrl", model.imageUrl,
                        "latitude", latitude1,
                        "longitude",longitude1,
                        "address", newAddress,
                        "foodtype", newFoodType,
                        "review", newReview,
                        "favourite", isFavorite)
                    .addOnSuccessListener {
                        model.apply {
                            name = newDestinationName
                            menu = newMenu
                            address = map_edittext.text.toString()
                            foodtype = newFoodType
                            review = newReview
                            imageUrl = model.imageUrl
                            favourite = isFavorite
                        }
                        destinationDao.update(model)
                        Toast.makeText(context, "Destination updated successfully", Toast.LENGTH_SHORT).show()
                        NavHostFragment.findNavController(this).popBackStack()
                    }
                    .addOnFailureListener { Toast.makeText(context, "Error updating destination", Toast.LENGTH_SHORT).show() }

            }else{
                selectedImageUri?.let { uri ->
                    val storageRef = FirebaseStorage.getInstance().getReference().child("destination_images/${newDestinationName}")
                    storageRef.putFile(uri)
                        .addOnSuccessListener { taskSnapshot ->
                            storageRef.downloadUrl.addOnSuccessListener { url ->
                                model.imageUrl = url.toString()
                                val latitude = if (selectedLocation != null) selectedLocation!!.latitude else model.latitude
                                val longitude = if (selectedLocation != null) selectedLocation!!.longitude else model.longitude

                                firebaseFirestore.collection("Destinations").document(model.key.toString())
                                    .update(
                                        "name", newDestinationName,
                                        "menu", newMenu,
                                        "imageUrl", url.toString(),
                                        "latitude", latitude,
                                        "longitude", longitude,
                                        "address", newAddress,
                                        "foodtype", newFoodType,
                                        "review", newReview,
                                        "favourite", isFavorite)
                                    .addOnSuccessListener {
                                        model.apply {
                                            name = newDestinationName
                                            menu = newMenu
                                            address = map_edittext.text.toString()
                                            foodtype = newFoodType
                                            review = newReview
                                            imageUrl = uri.toString()
                                            favourite = isFavorite
                                        }
                                        destinationDao.update(model)
                                        Toast.makeText(context, "Destination updated successfully", Toast.LENGTH_SHORT).show()
                                        NavHostFragment.findNavController(this).popBackStack()
                                    }
                                    .addOnFailureListener { Toast.makeText(context, "Error updating destination", Toast.LENGTH_SHORT).show() }

                                Toast.makeText(context, "Image updated successfully", Toast.LENGTH_SHORT).show()

                            }
                        }
                        .addOnFailureListener { Toast.makeText(context, "Error uploading image", Toast.LENGTH_SHORT).show() }
                }

            }

        }

        deleteButton.setOnClickListener {
            model.key?.let { destinationKey ->
                firebaseFirestore.collection("Destinations").document(destinationKey)
                    .delete()
                    .addOnSuccessListener {
                        destinationDao.delete(model)
                        Toast.makeText(context, "Destination deleted successfully", Toast.LENGTH_SHORT).show()
                        activity?.onBackPressed()
                    }
                    .addOnFailureListener { Toast.makeText(context, "Error deleting destination", Toast.LENGTH_SHORT).show() }
            }
        }

        return rootView
    }
    private fun selectAddress() {
        val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS)
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
            .build(requireContext())
        startActivityForResult(intent, 11)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 11) {
            if (resultCode == Activity.RESULT_OK) {
                val place = Autocomplete.getPlaceFromIntent(data!!)
                val address = place.address
                map_edittext.setText(address)
                val latitude = place.latLng?.latitude
                val longitude = place.latLng?.longitude
                if (latitude != null && longitude != null) {
                    selectedLocation = LatLng(latitude, longitude)
                    map.clear()
                    map.addMarker(MarkerOptions().position(selectedLocation!!).title(address))
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLocation!!, 15f))
                }
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                val status = Autocomplete.getStatusFromIntent(data!!)
            }
        }
    }
    private fun openGallery() {
        imagePickerLauncher.launch("image/*")
    }

    private fun setCurrentDestination(bundle: Bundle) {
        val destinationId = bundle.getString("destinationId")?: ""
        val userEmail = bundle.getString("userEmail")?: ""
        val name = bundle.getString("name")?: ""
        val imageUrl = bundle.getString("imageUrl")?: ""
        val menu = bundle.getString("menu")?: ""
        val latitude = bundle.getDouble("latitude")
        val longitude = bundle.getDouble("longitude")
        val address = bundle.getString("address")?: ""
        val foodtype = bundle.getString("foodtype")?: ""
        val review = bundle.getString("review")?: ""
        isFavorite = bundle.getBoolean("favourite") ?: false

        model =
            DestinationModel(
                key =  destinationId!!,
                userEmail =userEmail!!,
                imageUrl =imageUrl!!,
                name =name!!,
                menu =menu!!,
                latitude =latitude,
                longitude =longitude,
                address =address!!,
                foodtype =foodtype!!,
                review =review,
                favourite =isFavorite)
        if (latitude != 0.0 && longitude != 0.0) {
            val destinationPosition = LatLng(latitude, longitude)
            val marker = map.addMarker(MarkerOptions().position(destinationPosition).title(name).snippet(destinationId))

        }
        val isEdit = bundle.getBoolean("isEdit")
        if (!isEdit) {
            destinationNameEditText.isEnabled = false
            textMenu.isEnabled = false
            foodType.isEnabled = false
            deleteButton.visibility = View.GONE
            imageView.isEnabled = false
        }else{
            map_edittext.setOnClickListener { selectAddress() }
            reviewTextView.visibility = View.GONE
            reviewEditText.visibility = View.GONE
        }
        reviewEditText.visibility = View.GONE
    }

    override fun onMapReady(p0: GoogleMap) {
        TODO("Not yet implemented")
    }

}
