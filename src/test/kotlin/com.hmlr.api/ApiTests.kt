package com.hmlr.api


import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity

/**
 * @author Nitesh Solanki
 */

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApiTests() {

    @LocalServerPort
    private val port: Int = 8080

    @Autowired
    private val restTemplate: TestRestTemplate? = null

    private val Url: String = "http://localhost:$port/api/"

    @Test
    fun `me`() {
        val obj = restTemplate!!.getForObject(Url+"/me", MediaType.APPLICATION_JSON_VALUE::class.java)
        print(obj.toString())
    }

    @Test
    fun `createIOU`(){
        val response = restTemplate!!.put(Url+"/create-iou?iouValue=1&partyName=Conveyancer1", ResponseEntity::class.java)
        print(response)
    }

    @Test
    fun `getIOUs`() {
        val ious = restTemplate!!.getForObject(Url+"/ious", MediaType.APPLICATION_JSON_VALUE::class.java)
        print(ious)
    }
}
