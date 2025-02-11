package com.example.petsapp26

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.petsapp26.databinding.FragmentRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID
import java.security.MessageDigest

class RegisterUser : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    //private lateinit var auth: FirebaseAuth // Firebase Authentication instance

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        //auth = FirebaseAuth.getInstance() // Initialize Firebase Auth
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the spinner and set its adapter
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.role_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            //binding.roleSpinner.adapter = adapter
        }

        binding.register.setOnClickListener {
            val username = binding.username.text.toString().trim()
            val password = binding.password.text.toString().trim()
            val confirmPassword = binding.cfmPassword.text.toString().trim()
            val role = "user"

            if (validateForm(username, password, confirmPassword)) {
                registerUser(username, password, role)
            }
        }

        binding.loginLink.setOnClickListener {
            // Perform fragment transaction to show RegisterUser fragment
            fragmentManager?.beginTransaction()
                ?.replace(R.id.fragment_container, Login())
                ?.addToBackStack(null)
                ?.commit()
        }
    }

    private fun validateForm(username: String, password: String, confirmPassword: String): Boolean {
        // Check if any field is empty
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(context, "All fields are required.", Toast.LENGTH_SHORT).show()
            return false
        }

        // Check if passwords match
        if (password != confirmPassword) {
            Toast.makeText(context, "Passwords do not match.", Toast.LENGTH_SHORT).show()
            return false
        }

        // Check for password strength
        if (password.length < 8) {
            Toast.makeText(context, "Password must be at least 8 characters long.", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!password.matches(".*[A-Z].*".toRegex())) {
            Toast.makeText(context, "Password must contain at least one uppercase letter.", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!password.matches(".*[a-z].*".toRegex())) {
            Toast.makeText(context, "Password must contain at least one lowercase letter.", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!password.matches(".*[!@#\$%^&*].*".toRegex())) {
            Toast.makeText(context, "Password must contain at least one special character (!@#\$%^&*).", Toast.LENGTH_SHORT).show()
            return false
        }


        return true
    }

    private fun registerUser(username: String, password: String, role: String) {

        val hashedPassword = hashPassword(password) // Hash the password

        // First, check if the username already exists
        val usersCollection = FirebaseFirestore.getInstance().collection("users")
        usersCollection.whereEqualTo("username", username)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {

                    val uid = UUID.randomUUID().toString()
                    // Username does not exist, proceed with registration
                    val userData = hashMapOf(
                        "uid" to uid,
                        "username" to username,
                        "password" to hashedPassword,
                        "user" to role
                    )
                    usersCollection.add(userData)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Registration successful. Please log in.", Toast.LENGTH_LONG).show()
                            navigateToLogin()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(context, "Registration failed: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    // Username already exists, prompt the user to choose a different username
                    Toast.makeText(context, "Username already exists. Please choose a different username.", Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Error checking username uniqueness: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun navigateToLogin() {
        fragmentManager?.beginTransaction()
            ?.replace(R.id.fragment_container, Login())
            ?.commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun hashPassword(password: String): String {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("", { str, it -> str + "%02x".format(it) })
    }
}
