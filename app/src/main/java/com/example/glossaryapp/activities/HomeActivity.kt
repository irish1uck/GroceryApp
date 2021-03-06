package com.example.glossaryapp.activities

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.core.view.MenuItemCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.glossaryapp.R
import com.example.glossaryapp.adapters.AdapterCategory
import com.example.glossaryapp.app.Endpoints
import com.example.glossaryapp.database.DBHelper
import com.example.glossaryapp.helpers.SessionManager
import com.example.glossaryapp.models.Category
import com.example.glossaryapp.models.CategoryResult
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.app_bar.*
import kotlinx.android.synthetic.main.layout_menu_cart.view.*
import kotlinx.android.synthetic.main.nav_header.view.*

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    var textViewShoppingCartCount: TextView? = null

    lateinit var myDrawerLayout: DrawerLayout
    lateinit var myNavView: NavigationView
    private lateinit var sessionManager: SessionManager
    lateinit var adapterCategory: AdapterCategory
    lateinit var dbHelper: DBHelper
    var myList: ArrayList<Category> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        dbHelper = DBHelper(this)
        sessionManager = SessionManager(this)
        init()
    }

    private fun init() {
        setupToolbar()
        getData()
        myDrawerLayout = drawer_layout
        myNavView = nav_view
        var firstName: String? = null
        var email: String? = null
        if (sessionManager.getQuickLogin()) {
            firstName = sessionManager.getFirstName()
            email = sessionManager.getEmail()
        } else {
            firstName = "Guest!"
            email = "Please sign in to Purchase"
        }

        var myHeaderView = myNavView.getHeaderView(0)
        myHeaderView.text_view_header_firstName.text = firstName
        myHeaderView.text_view_header_email.text = email

        var myToggle = ActionBarDrawerToggle(this, myDrawerLayout, toolbar, 0, 0)
        myDrawerLayout.addDrawerListener(myToggle)
        myToggle.syncState()
        myNavView.setNavigationItemSelectedListener(this)


        adapterCategory = AdapterCategory(this)
        recycler_view.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recycler_view.adapter = adapterCategory

        image_view_home_headline_image.setImageResource(R.drawable.app_logo)
    }


    private fun getData() {
        var request = StringRequest(Request.Method.GET, Endpoints.getCategory(), {
            var gson = Gson()
            var categoryResult = gson.fromJson(it, CategoryResult::class.java)
            adapterCategory.setData(categoryResult.data)

            Toast.makeText(
                applicationContext,
                "Category Images are Loading...",
                Toast.LENGTH_SHORT
            ).show()
            progress_bar.visibility = View.GONE
        },
            {

            })
        Volley.newRequestQueue(this).add(request)
    }

    private fun setupToolbar() {
        var toolbar = toolbar
        toolbar.title = "RäGN Home"
        setSupportActionBar(toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_cart, menu)
        var item = menu.findItem(R.id.action_cart)
        MenuItemCompat.setActionView(item, R.layout.layout_menu_cart)
        var view = MenuItemCompat.getActionView(item)
        textViewShoppingCartCount = view.text_view_cart_count
        view.setOnClickListener {
            startActivity(Intent(applicationContext, ShoppingCartActivity::class.java))
        }
        updateShoppingCartCount()
        return true
    }

    private fun updateShoppingCartCount() {
        var myCount = dbHelper.getCartTotalCount()
        if(myCount == 0) {
            textViewShoppingCartCount?.visibility = View.INVISIBLE
        } else {
            textViewShoppingCartCount?.visibility = View.VISIBLE
            textViewShoppingCartCount?.text = myCount.toString()
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_profile -> {
                if(sessionManager.getQuickLogin()){
                    startActivity(Intent(this, ProfileActivity::class.java))
                } else {
                    startActivity(Intent(this, LoginActivity::class.java))
                }
            }
            R.id.item_address -> {
                if(sessionManager.getQuickLogin()){
                    startActivity(Intent(this, AddressActivity::class.java))
                } else {
                    startActivity(Intent(this, LoginActivity::class.java))
                }
            }
            R.id.item_orders -> startActivity(Intent(this, OrdersActivity::class.java))
            R.id.item_logout ->  if(sessionManager.getQuickLogin()){
                dialogLogout()
            } else {
                Toast.makeText(this, "You're not not logged in", Toast.LENGTH_SHORT).show()
            }
        }
        myDrawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {

        if (myDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            myDrawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun dialogLogout() {
        var builder = AlertDialog.Builder(this)
        builder.setTitle("Confirm Log Out")
        builder.setMessage("Are you sure you want to log out?")
        builder.setNegativeButton("No", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, p1: Int) {
                dialog?.dismiss()
            }
        })
        builder.setPositiveButton("Yes", object : DialogInterface.OnClickListener {
            override fun onClick(p0: DialogInterface?, p1: Int) {
                sessionManager.logout()
                startActivity(Intent(applicationContext, HomeActivity::class.java))
            }
        })
        var myAlertDialog = builder.create()
        myAlertDialog.show()
    }

}