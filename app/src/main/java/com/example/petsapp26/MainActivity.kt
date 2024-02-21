package com.example.petsapp26

import android.content.Context
import android.os.Bundle
import com.google.firebase.firestore.FirebaseFirestore
import com.google.android.material.navigation.NavigationView

import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener{

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var toolbar: Toolbar // Define toolbar variable


    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        drawerLayout = findViewById(R.id.drawer_layout)

        val userRole = getUserRole() // Retrieve the user's role
        updateNavigationView(userRole) // Update the navigation view based on the role

        toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)
        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()


        toggle.isDrawerIndicatorEnabled = false
        // Disable navigation drawer initially
        disableNavigationDrawer()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, Login()).commit()
            navigationView.setCheckedItem(R.id.nav_login)
        }
    }


//    private fun fetchUserRole() {
//        val userId =  // method to get current user's ID
//            firestore.collection("users").document(userId).get()
//                .addOnSuccessListener { documentSnapshot ->
//                    val userRole = documentSnapshot.getString("role") ?: "user" // default to "user" if not found
//                    updateNavigationView(userRole)
//                }
//                .addOnFailureListener { e ->
//                    Log.e("MainActivity", "Error fetching user role", e)
//                }
//    }

    fun updateNavigationView(userRole: String?) {
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.menu.clear() // Clear existing items

        when (userRole) {
            "admin" -> {
                navigationView.inflateMenu(R.menu.nav_menu_admin) // load admin menu
            }
            "user" -> {
                navigationView.inflateMenu(R.menu.nav_menu_user) // load user menu
            }
            else -> {
                // Handle other roles or default case
                navigationView.inflateMenu(R.menu.nav_menu_admin) // default to user menu
            }
        }
    }

    private fun getUserRole(): String {
        val preferences = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        return preferences.getString("userRole", "user") ?: "user" // Default to "user"
    }




    fun disableNavigationDrawer() {
        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        toggle.isDrawerIndicatorEnabled = false
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }


    fun enableNavigationDrawer() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav)
        toggle.isDrawerIndicatorEnabled = true
        drawerLayout.addDrawerListener(toggle)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        toggle.syncState()
        // Enable the toggle
        //toggle.isDrawerIndicatorEnabled = true
        //supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

        /*super.onCreate(savedInstanceState)
        setContent {
            PetsApp26Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                    // Example: Adding a new document to Firestore
                    //addNewUser("newUsername", "newPassword")
                }
            }
        }
    }*/

    /*private fun addNewUser(username: String, password: String) {
        // Reference to the "users" collection (you can change "users" to your collection name)
        val usersCollection = firestore.collection("users")

        // Creating a new document with a unique auto-generated ID
        val newDocumentRef = usersCollection.document()

        // Adding data to the document
        val userData = hashMapOf(
            "username" to username,
            "password" to password
        )

        // Set the data to the Firestore document
        newDocumentRef.set(userData)
            .addOnSuccessListener {
                // Document added successfully
                // You can handle success scenarios here
                println("Document added successfully!")
            }
            .addOnFailureListener { e ->
                // Handle errors here
                println("Error adding document: $e")
            }
    }
}

    @Composable
    fun Greeting(name: String, modifier: Modifier = Modifier) {
        Text(
            text = "Hello $name!",
            modifier = modifier
        )
    }

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        PetsApp26Theme {
            Greeting("Android")
        }
    }*/

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_login -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, Login()).commit()
            R.id.nav_chat -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, Chat()).commit()
            R.id.nav_home -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment()).commit()
            R.id.nav_settings -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SettingsFragment()).commit()
            R.id.nav_share -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ShareFragment()).commit()
            R.id.nav_about -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AboutFragment()).commit()
            R.id.nav_logout -> {
                // Sign out from Firebase Authentication
                FirebaseAuth.getInstance().signOut()

                // Clear any saved user data (e.g., SharedPreferences)
                val sharedPreferences = getSharedPreferences("YourSharedPrefs", Context.MODE_PRIVATE)
                sharedPreferences.edit().clear().apply()

                // Display logout message
                Toast.makeText(this, "Logged out!", Toast.LENGTH_SHORT).show()

                // Disable navigation drawer
                disableNavigationDrawer()

                // Replace the fragment with the login fragment
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, Login())
                    .commit()

                // Clear back stack to prevent going back to the secured fragments
                supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}