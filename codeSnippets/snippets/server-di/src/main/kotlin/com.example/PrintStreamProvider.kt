package com.example

import java.io.PrintStream

fun stdout(): () -> PrintStream = { System.out }