package com.deyvidandrades.meusfeeds.interfaces

interface Objeto {
    fun toMap(): Map<String, Any>
    fun compareTo(o: Any): Int
}