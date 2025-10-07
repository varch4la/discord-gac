package com.github.varch4la.dgac.svg;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class StatusCardFactory {

	private final Document cardTemplate;
	private final Transformer transformer;

	public StatusCardFactory() {
		try (InputStream in = StatusCardFactory.class.getResourceAsStream("/template.svg")) {
			this.cardTemplate = DocumentBuilderFactory.newDefaultInstance().newDocumentBuilder().parse(in);
			this.transformer = TransformerFactory.newDefaultInstance().newTransformer();
		} catch (IOException | SAXException | ParserConfigurationException | TransformerConfigurationException e) {
			throw new IllegalStateException(e);
		}
	}

	public String createStatusCard() throws IOException, TransformerException {
		try (ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
			transformer.transform(new DOMSource(cardTemplate), new StreamResult(buffer));
			return new String(buffer.toByteArray(), StandardCharsets.UTF_8);
		}
	}
}
