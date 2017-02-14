package com.bmstu.coursework.gef4;

import java.io.FileReader;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.eclipse.gef4.fx.nodes.GeometryNode;
import org.eclipse.gef4.fx.nodes.InfiniteCanvas;
import org.eclipse.gef4.geometry.euclidean.Angle;
import org.eclipse.gef4.geometry.planar.Arc;
import org.eclipse.gef4.geometry.planar.Ellipse;
import org.eclipse.gef4.geometry.planar.Line;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.geometry.planar.RoundedRectangle;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 *
 *
 * Instance of this class reads data and draws shapes
 *
 */
public class XmlReader
    extends Application {

    private static final String HEIGHT = "h";
    private static final String WIDTH = "w";
    private static final String RADIUS_Y = "ry";
    private static final String RADIUS_X = "rx";
    private static final String NAME = "name";
    private static final String Y_COORDINATE = "y";
    private static final String X_COORDINATE = "x";
    private static final int ARC_SIZE = 10;
    private static final int SHAPE_OFFSET = 120;
    private static final int FIGURES_OFFSET_Y = 20;
    private static final String FILE_NAME = "shapesMin.txt";
    private static final String TITLE = "Coursework";

    private InfiniteCanvas canvas;
    private int currentPositionX;
    private int currentPositionY;
    private int xOffset;

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setScene(createScene());
        primaryStage.sizeToScene();
        primaryStage.setTitle(TITLE);
        primaryStage.show();
    }

    private Scene createScene() {
        canvas = new InfiniteCanvas();
        Scene scene = new Scene(canvas, 600, 200);
        readXMLFile();
        canvas.setVerticalScrollOffset(FIGURES_OFFSET_Y);

        return scene;
    }

    private void readXMLFile() {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        try
        {
            XMLEventReader eventReader = factory.createXMLEventReader(new FileReader(FILE_NAME));
            while (eventReader.hasNext())
            {
                XMLEvent event = eventReader.nextEvent();
                switch (event.getEventType()) {
                case XMLStreamConstants.START_ELEMENT:
                    processStartElement(event.asStartElement());
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    processEndElement(event.asEndElement());
                    break;

                default:
                    break;
                }
            }
        }
        catch (Exception e)
        {
            // Do nothing
        }
    }

    private void processStartElement(StartElement startElement) {
        String startElementName = startElement.getName().getLocalPart();

        if ("move".equals(startElementName))
        {
            currentPositionX = Integer.valueOf(startElement.getAttributeByName(new QName(X_COORDINATE)).getValue());
            currentPositionY = Integer.valueOf(startElement.getAttributeByName(new QName(Y_COORDINATE)).getValue());

            return;
        }
        else
        {
            Node shape = null;
            switch (startElementName) {
            case "shape":
                processShapeStart(startElement);
                break;

            case "arc":
                shape = createArc(startElement);
                break;

            case "line":
                shape = createLine(startElement);
                break;

            case "ellipse":
                shape = createEllipse(startElement);
                break;

            case "rect":
                shape = createRectangle(startElement);
                break;

            case "roundrect":
                shape = createRoundRectangle(startElement);
                break;

            default:
                break;
            }

            if (shape != null)
            {
                canvas.getContentGroup().getChildren().add(shape);
            }
        }
    }

    private void processEndElement(EndElement endElement) {
        String endElementName = endElement.getName().getLocalPart();
        if ("shape".equals(endElementName))
        {
            xOffset += SHAPE_OFFSET;
        }
    }

    private void processShapeStart(StartElement startElement) {
        String name = startElement.getAttributeByName(new QName(NAME)).getValue();
        Label nameLabel = new Label(name);
        nameLabel.setLayoutX(xOffset);
        nameLabel.setLayoutY(-FIGURES_OFFSET_Y);
        canvas.getContentGroup().getChildren().add(nameLabel);
    }

    private Node createArc(StartElement startElement) {
        int rx = Integer.valueOf(startElement.getAttributeByName(new QName(RADIUS_X)).getValue());
        int ry = Integer.valueOf(startElement.getAttributeByName(new QName(RADIUS_Y)).getValue());

        GeometryNode<Arc> arc = new GeometryNode<>(new Arc(currentPositionX + xOffset - rx, currentPositionY - ry,
            2 * rx, 2 * ry, Angle.fromDeg(0), Angle.fromDeg(180)));

        return arc;
    }

    private Node createLine(StartElement startElement) {
        int x = Integer.valueOf(startElement.getAttributeByName(new QName(X_COORDINATE)).getValue());
        int y = Integer.valueOf(startElement.getAttributeByName(new QName(Y_COORDINATE)).getValue());

        GeometryNode<Line> line =
            new GeometryNode<>(new Line(currentPositionX + xOffset, currentPositionY, x + xOffset, y));
        currentPositionX = x;
        currentPositionY = y;

        return line;
    }

    private Node createEllipse(StartElement startElement) {
        int x = Integer.valueOf(startElement.getAttributeByName(new QName(X_COORDINATE)).getValue());
        int y = Integer.valueOf(startElement.getAttributeByName(new QName(Y_COORDINATE)).getValue());
        int width = Integer.valueOf(startElement.getAttributeByName(new QName(WIDTH)).getValue());
        int height = Integer.valueOf(startElement.getAttributeByName(new QName(HEIGHT)).getValue());

        GeometryNode<Ellipse> ellipse = new GeometryNode<>(new Ellipse(xOffset + x, y, width, height));
        ellipse.setFill(Color.WHITE);
        ellipse.setStroke(Color.BLACK);

        return ellipse;
    }

    private Node createRectangle(StartElement startElement) {
        int x = Integer.valueOf(startElement.getAttributeByName(new QName(X_COORDINATE)).getValue());
        int y = Integer.valueOf(startElement.getAttributeByName(new QName(Y_COORDINATE)).getValue());
        int width = Integer.valueOf(startElement.getAttributeByName(new QName(WIDTH)).getValue());
        int height = Integer.valueOf(startElement.getAttributeByName(new QName(HEIGHT)).getValue());

        GeometryNode<Rectangle> rectangle = new GeometryNode<>(new Rectangle(xOffset + x, y, width, height));
        rectangle.setFill(Color.WHITE);
        rectangle.setStroke(Color.BLACK);

        return rectangle;
    }

    private Node createRoundRectangle(StartElement startElement) {
        int x = Integer.valueOf(startElement.getAttributeByName(new QName(X_COORDINATE)).getValue());
        int y = Integer.valueOf(startElement.getAttributeByName(new QName(Y_COORDINATE)).getValue());
        int width = Integer.valueOf(startElement.getAttributeByName(new QName(WIDTH)).getValue());
        int height = Integer.valueOf(startElement.getAttributeByName(new QName(HEIGHT)).getValue());

        GeometryNode<RoundedRectangle> roundedRectangle =
            new GeometryNode<>(new RoundedRectangle(xOffset + x, y, width, height, ARC_SIZE, ARC_SIZE));
        roundedRectangle.setFill(Color.WHITE);
        roundedRectangle.setStroke(Color.BLACK);

        return roundedRectangle;
    }
}
