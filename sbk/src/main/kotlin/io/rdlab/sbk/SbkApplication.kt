package io.rdlab.sbk

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SbkApplication

fun main(args: Array<String>) {
	runApplication<SbkApplication>(*args)
}
