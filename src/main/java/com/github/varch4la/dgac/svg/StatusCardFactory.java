package com.github.varch4la.dgac.svg;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.github.varch4la.dgac.presence.PresenceActivity;

import net.dv8tion.jda.api.entities.Member;

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

	private Node findNodeById(Node parent, String id) {
		NodeList list = parent.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);
			NamedNodeMap attr = node.getAttributes();
			if (attr != null) {
				Node idNode = attr.getNamedItem("id");
				if (idNode != null) {
					String nid = idNode.getNodeValue();
					if (nid.equals(id)) {
						return node;
					}
				}
			}
			Node found = findNodeById(node, id);
			if (found != null)
				return found;
		}
		return null;
	}

	private Document modifyDocument(String nickname, String username, String app, String line2, String line1,
			String time) {
		Document copy = (Document) cardTemplate.cloneNode(true);

		findNodeById(copy, "nickname").setTextContent(nickname);
		findNodeById(copy, "username").setTextContent(username);
		findNodeById(copy, "actname").setTextContent(app);
		findNodeById(copy, "actline1").setTextContent(line1);
		findNodeById(copy, "actline2").setTextContent(line2);
		findNodeById(copy, "acttime").setTextContent(time);

		return copy;
	}

	public String createStatusCard(Member member, PresenceActivity act) throws IOException, TransformerException {
		try (ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
			Duration dur = act == null ? null : Duration.between(Instant.ofEpochMilli(act.startTime()), Instant.now());
			transformer.transform(
					new DOMSource(modifyDocument(member.getEffectiveName(), member.getUser().getName(),
							act == null ? "Not playing anything" : act.name(), act == null ? "-" : act.state(),
							act == null ? "-" : act.details() == null ? "" : act.details(),
							dur == null ? "-" : fmt(dur.toMinutesPart()) + ":" + fmt(dur.toSecondsPart()))),
					new StreamResult(buffer));
			return new String(buffer.toByteArray(), StandardCharsets.UTF_8);
		}
	}

	private static String fmt(int time) {
		return (time < 10 ? "0" : "") + String.valueOf(time);
	}
}
