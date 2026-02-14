package com.example

interface Database

class PostgresDatabase(val url: String) : Database