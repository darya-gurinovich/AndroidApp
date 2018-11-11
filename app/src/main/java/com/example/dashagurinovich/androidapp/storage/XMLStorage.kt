package com.example.dashagurinovich.androidapp.storage

import android.net.Uri
import com.example.dashagurinovich.androidapp.model.Profile
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import java.io.File
import java.lang.Exception
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

class XMLStorage(private val file: File) : IStorage {

    override fun saveProfile(profile: Profile) {
        if (!file.exists()) file.createNewFile()

        val factory = DocumentBuilderFactory.newInstance()
        factory.isNamespaceAware = false
        factory.isValidating = false
        val builder = factory.newDocumentBuilder()


        val document : Document = try {
            builder.parse(file)
        }
        catch (ex: Exception) {
            builder.newDocument()
        }

        var data = document.getElementsByTagName("data")
        if (data.length == 0) {
            val dataNode = document.createElement("data")
            document.appendChild(dataNode)

            data = document.getElementsByTagName("data")
        }

        var profileNodes = document.getElementsByTagName("profile")
        if (profileNodes.length == 0){
            val profileNode = document.createElement("profile")
            data.item(0).appendChild(profileNode)

            profileNodes = document.getElementsByTagName("profile")
        }

        val profileNode = profileNodes.item(0) as Element
        profileNode.setAttribute("surname", profile.surname)
        profileNode.setAttribute("name", profile.name)
        profileNode.setAttribute("email", profile.email)
        profileNode.setAttribute("phone", profile.phone)

        val transformer = TransformerFactory.newInstance().newTransformer()
        transformer.setOutputProperty(OutputKeys.INDENT, "yes")
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "3")

        transformer.transform(DOMSource(document), StreamResult(file))
    }

    override fun getProfile() : Profile? {
        if (!file.exists()) return null

        // The builder is used to parse XML documents
        val factory = DocumentBuilderFactory.newInstance()
        factory.isNamespaceAware = false
        factory.isValidating = false
        val builder = factory.newDocumentBuilder()

        val document = builder.parse(file)
        val data = document.documentElement

        val profile : Profile? = Profile()

        val dataSections = data.childNodes
        for (index in 0..dataSections.length) {
            if (dataSections.item(index) == null) continue
            if (dataSections.item(index).nodeType != Node.ELEMENT_NODE) continue
            val dataSection = dataSections.item(index) as Element
            when (dataSection.nodeName) {
                "profile" -> {
                    profile?.surname = dataSection.getAttribute("surname")
                    profile?.name = dataSection.getAttribute("name")
                    profile?.email = dataSection.getAttribute("email")
                    profile?.phone = dataSection.getAttribute("phone")
                    profile?.imagePath = dataSection.getAttribute("photo")
                }
            }
        }

        return profile
    }

    override fun savePhoto(photoPath: String) {
        if (!file.exists()) file.createNewFile()

        val factory = DocumentBuilderFactory.newInstance()
        factory.isNamespaceAware = false
        factory.isValidating = false
        val builder = factory.newDocumentBuilder()

        val document : Document = try {
            builder.parse(file)
        }
        catch (ex: Exception) {
            builder.newDocument()
        }

        var data = document.getElementsByTagName("data")
        if (data.length == 0) {
            val dataNode = document.createElement("data")
            document.appendChild(dataNode)

            data = document.getElementsByTagName("data")
        }

        var profile = document.getElementsByTagName("profile")
        if (profile.length == 0){
            val profileNode = document.createElement("profile")
            data.item(0).appendChild(profileNode)

            profile = document.getElementsByTagName("profile")
        }

        (profile.item(0) as Element).setAttribute("photo", photoPath)

        val transformer = TransformerFactory.newInstance().newTransformer()
        transformer.setOutputProperty(OutputKeys.INDENT, "yes")
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "3")

        transformer.transform(DOMSource(document), StreamResult(file))
    }
}