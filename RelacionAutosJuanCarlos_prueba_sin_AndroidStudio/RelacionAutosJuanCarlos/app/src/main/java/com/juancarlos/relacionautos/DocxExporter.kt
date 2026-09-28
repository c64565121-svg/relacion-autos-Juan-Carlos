package com.juancarlos.relacionautos

import com.juancarlos.relacionautos.data.Vehicle
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object DocxExporter {
    fun export(vehicle: Vehicle, output: File) {
        output.parentFile?.mkdirs()

        val photo = File(vehicle.photoPath)
        val hasPhoto = photo.exists()

        ZipOutputStream(FileOutputStream(output)).use { zip ->
            put(zip, "[Content_Types].xml", contentTypes(hasPhoto))
            put(zip, "_rels/.rels", rootRels())
            put(zip, "word/document.xml", documentXml(vehicle, hasPhoto))
            put(zip, "word/_rels/document.xml.rels", documentRels(hasPhoto))

            if (hasPhoto) {
                zip.putNextEntry(ZipEntry("word/media/photo.jpg"))
                FileInputStream(photo).use { it.copyTo(zip) }
                zip.closeEntry()
            }
        }
    }

    private fun put(zip: ZipOutputStream, name: String, text: String) {
        zip.putNextEntry(ZipEntry(name))
        zip.write(text.toByteArray(Charsets.UTF_8))
        zip.closeEntry()
    }

    private fun esc(s: String): String =
        s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")

    private fun p(text: String, bold: Boolean = false): String {
        val runProps = if (bold) "<w:rPr><w:b/></w:rPr>" else ""
        return "<w:p><w:r>$runProps<w:t xml:space=\"preserve\">${esc(text)}</w:t></w:r></w:p>"
    }

    private fun documentXml(v: Vehicle, hasPhoto: Boolean): String {
        val image = if (hasPhoto) """
            <w:p><w:r><w:drawing>
              <wp:inline xmlns:wp="http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing"
                distT="0" distB="0" distL="0" distR="0">
                <wp:extent cx="4572000" cy="2571750"/>
                <wp:docPr id="1" name="Foto del vehículo"/>
                <a:graphic xmlns:a="http://schemas.openxmlformats.org/drawingml/2006/main">
                  <a:graphicData uri="http://schemas.openxmlformats.org/drawingml/2006/picture">
                    <pic:pic xmlns:pic="http://schemas.openxmlformats.org/drawingml/2006/picture">
                      <pic:nvPicPr><pic:cNvPr id="0" name="photo.jpg"/><pic:cNvPicPr/></pic:nvPicPr>
                      <pic:blipFill>
                        <a:blip xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships" r:embed="rId1"/>
                        <a:stretch><a:fillRect/></a:stretch>
                      </pic:blipFill>
                      <pic:spPr>
                        <a:xfrm><a:off x="0" y="0"/><a:ext cx="4572000" cy="2571750"/></a:xfrm>
                        <a:prstGeom prst="rect"><a:avLst/></a:prstGeom>
                      </pic:spPr>
                    </pic:pic>
                  </a:graphicData>
                </a:graphic>
              </wp:inline>
            </w:drawing></w:r></w:p>
        """.trimIndent() else ""

        return """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
        <w:document xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main">
          <w:body>
            ${p("RELACIÓN DE LOS AUTOS JUAN CARLOS", true)}
            $image
            ${p("Número de chasis: ${v.chassis}")}
            ${p("Modelo: ${v.model}")}
            ${p("Año: ${v.year}")}
            ${p("Mecánico: ${v.mechanic}")}
            ${p("Observaciones: ${v.observations}")}
            <w:sectPr/>
          </w:body>
        </w:document>""".trimIndent()
    }

    private fun rootRels() = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
    <Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
      <Relationship Id="rId1"
        Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument"
        Target="word/document.xml"/>
    </Relationships>""".trimIndent()

    private fun documentRels(hasPhoto: Boolean) =
        if (hasPhoto) """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
        <Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
          <Relationship Id="rId1"
            Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/image"
            Target="media/photo.jpg"/>
        </Relationships>""".trimIndent()
        else """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
        <Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships"/>""".trimIndent()

    private fun contentTypes(hasPhoto: Boolean): String {
        val img = if (hasPhoto) """<Default Extension="jpg" ContentType="image/jpeg"/>""" else ""
        return """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
        <Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">
          <Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/>
          <Default Extension="xml" ContentType="application/xml"/>
          $img
          <Override PartName="/word/document.xml"
            ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml"/>
        </Types>""".trimIndent()
    }
}
