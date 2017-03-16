package com.skcraft.launcher.swing;

import javax.swing.text.html.*;
import javax.swing.text.html.ParagraphView;
import javax.swing.text.*;
import javax.swing.text.FlowView.FlowStrategy;

/**
 * Better performing HTMLEditorKit
 * http://java-sl.com/JEditorPanePerformance.html
 */
public class WebpagePanelEditorKit extends HTMLEditorKit {
    ViewFactory factory = new WebpagePanelViewFactory();
    
    public ViewFactory getViewFactory() {
        return factory;
    }
 
    class WebpagePanelViewFactory extends HTMLFactory {
        public View create(Element elem) {
            AttributeSet attrs = elem.getAttributes();
            Object elementName = attrs.getAttribute(AbstractDocument.ElementNameAttribute);
            Object o = (elementName != null) ? null : attrs.getAttribute(StyleConstants.NameAttribute);
            if (o instanceof HTML.Tag) {
                HTML.Tag kind = (HTML.Tag) o;
                if (kind == HTML.Tag.HTML) {
                    return new HTMLBlockView(elem);
                }
                else if (kind == HTML.Tag.IMPLIED) {
                    String ws = (String) elem.getAttributes().getAttribute(CSS.Attribute.WHITE_SPACE);
                    if ((ws != null) && ws.equals("pre")) {
                        return super.create(elem);
                    }
                    return new HTMLParagraphView(elem);
                } else if ((kind == HTML.Tag.P) ||
                        (kind == HTML.Tag.H1) ||
                        (kind == HTML.Tag.H2) ||
                        (kind == HTML.Tag.H3) ||
                        (kind == HTML.Tag.H4) ||
                        (kind == HTML.Tag.H5) ||
                        (kind == HTML.Tag.H6) ||
                        (kind == HTML.Tag.DT)) {
                    return new HTMLParagraphView(elem);
                }
            }
            return super.create(elem);
        }
 
    }
    
    class HTMLBlockView extends BlockView {

        public HTMLBlockView(Element elem) {
            super(elem,  View.Y_AXIS);
        }
     
        protected void layout(int width, int height) {
            //long start=System.currentTimeMillis();
            if (width<Integer.MAX_VALUE) {
                super.layout(width, height);
            }
            //long end=System.currentTimeMillis();
            //System.out.println("w="+width+" h="+height+" time="+(end-start));
        }
    }
    
    class HTMLParagraphView extends javax.swing.text.html.ParagraphView {     
        public HTMLParagraphView(Element elem) {
            super(elem);
            strategy = new HTMLFlowStrategy();
        }
     
        public int getResizeWeight(int axis) {
            return 0;
        }
    }
    
    public static class HTMLFlowStrategy extends FlowStrategy {
        public static final int MAX_VIEW_SIZE = 100;
        protected View createView(FlowView fv, int startOffset, int spanLeft, int rowIndex) {
            View res=super.createView(fv, startOffset, spanLeft, rowIndex);
            if (res.getEndOffset()-res.getStartOffset()> MAX_VIEW_SIZE) {
                res = res.createFragment(startOffset, startOffset+ MAX_VIEW_SIZE);
            }
            return res;
        }

    }
}
