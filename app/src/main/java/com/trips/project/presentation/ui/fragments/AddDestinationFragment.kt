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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.trips.project.R
import com.trips.project.data.db.DestinationDatabse
import com.trips.project.data.model.DestinationModel
import com.squareup.picasso.Picasso
import me.ibrahimsn.lib.SmoothBottomBar

class AddDestinationFragment : Fragment(), OnMapReadyCallback {
    private lateinit var destinationTitleEditText: EditText
    private lateinit var destinationDescriptionEditText: EditText
    private lateinit var destinationImageView: ImageView
    private lateinit var foodtypeEditText: EditText
    private lateinit var map_edittext: EditText
    private lateinit var addDestinationButton: Button
    private lateinit var cancelButton: Button
    private lateinit var map: GoogleMap
    private var selectedImageUri: Uri? = null
    private var selectedLocation: LatLng? = null

    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var localDb: DestinationDatabse
    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { result ->
        if (result != null) {
            try {
                result?.let {
                    Picasso.get().load(it).into(destinationImageView, object : com.squareup.picasso.Callback {
                        override fun onSuccess() {
                            selectedImageUri = it
                        }
                        override fun onError(e: Exception?) {
                            Toast.makeText(requireContext(), "Failed to load image", Toast.LENGTH_SHORT).show()
                            e?.printStackTrace()
                        }
                    })
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private val AUTOCOMPLETE_REQUEST_CODE = 1

    private fun selectAddress() {
        val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS)
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
            .build(requireContext())
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
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
        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        localDb = DestinationDatabse.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_add_destination, container, false)
        initializeViews(rootView)
        setupMapFragment()
        setupListeners()
        return rootView
    }

    private fun initializeViews(rootView: View) {
        destinationTitleEditText = rootView.findViewById(R.id.destinationname_textview)
        destinationDescriptionEditText = rootView.findViewById(R.id.destinationdescription_textview)
        destinationImageView = rootView.findViewById(R.id.destinationImage)
        foodtypeEditText = rootView.findViewById(R.id.date_edittext)
        map_edittext = rootView.findViewById(R.id.map_edittext)
        addDestinationButton = rootView.findViewById(R.id.add_button)
        cancelButton = rootView.findViewById(R.id.cancel_button)

    }

    private fun setupMapFragment() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapsFragment) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    private fun setupListeners() {
        destinationImageView.setOnClickListener { selectImage() }
        addDestinationButton.setOnClickListener { uploadDestinationData() }
        cancelButton.setOnClickListener { NavHostFragment.findNavController(this).popBackStack() }
        map_edittext.setOnClickListener { selectAddress() }
    }

    private fun selectImage() {
        imagePickerLauncher.launch("image/*")
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
    }

    private fun uploadDestinationData() {
        val name = destinationTitleEditText.text.toString().trim()
        val menu = destinationDescriptionEditText.text.toString().trim()
        val address = map_edittext.text.toString().trim()
        val foodtype = foodtypeEditText.text.toString().trim()

        if (name.isEmpty() || menu.isEmpty() || selectedImageUri == null || selectedLocation == null || address.isEmpty() || foodtype.isEmpty()) {
            Toast.makeText(requireContext(), "All fields and location must be filled", Toast.LENGTH_SHORT).show()
            return
        }
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Adding destination...")
        progressDialog.setCancelable(false)
        progressDialog.show()
        // First, upload the image to Firebase Storage
        val imageRef: StorageReference = storage.reference.child("destination_images/${name}.jpg")
        imageRef.putFile(selectedImageUri!!)
            .addOnSuccessListener { taskSnapshot ->
                imageRef.downloadUrl
                    .addOnSuccessListener { uri ->
                        // Then, save destination data including the image URL to Firestore
                        val destination = hashMapOf<String, Any>()
                        destination["name"] = name
                        destination["menu"] = menu
                        destination["imageUrl"] = uri.toString()
                        destination["latitude"] = selectedLocation!!.latitude
                        destination["longitude"] = selectedLocation!!.longitude
                        destination["userEmail"] = FirebaseAuth.getInstance().currentUser?.email!!
                        destination["address"] = address
                        destination["foodtype"] = foodtype
                        destination["review"] = ""
                        destination["favourite"] = false

                        firestore.collection("Destinations")
                            .add(destination)
                            .addOnSuccessListener { documentReference ->
                                progressDialog.dismiss()
                                Toast.makeText(requireContext(), "Destination added successfully", Toast.LENGTH_SHORT).show()
                                NavHostFragment.findNavController(this).popBackStack()

                                // Optionally, you can also save this destination data to Room database for offline access
                                saveDestinationToLocalDatabase(
                                    DestinationModel(
                                        documentReference.id,
                                        FirebaseAuth.getInstance().currentUser?.email!!,
                                        uri.toString(),
                                        name,
                                        menu,
                                        selectedLocation!!.latitude,
                                        selectedLocation!!.longitude,
                                        address,
                                        foodtype,
                                        "",
                                        false
                                    )
                                )
                                NavHostFragment.findNavController(this).popBackStack()
                            }
                            .addOnFailureListener { e ->
                                progressDialog.dismiss()
                                Toast.makeText(requireContext(), "Failed to add destination", Toast.LENGTH_SHORT).show()
                            }
                    }
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(requireContext(), "Failed to upload image", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveDestinationToLocalDatabase(destination: DestinationModel) {
        Thread { localDb.destinationDao().insert(destination) }.start()
    }
}
