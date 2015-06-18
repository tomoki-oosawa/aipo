package info.bliki.wiki.filter;

import info.bliki.htmlcleaner.ContentToken;
import info.bliki.htmlcleaner.Utils;
import info.bliki.wiki.model.Configuration;
import info.bliki.wiki.model.IWikiModel;
import info.bliki.wiki.tags.HTMLTag;
import info.bliki.wiki.tags.WPTag;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WPCodeBlock extends WPTag {

  private String contents = "";

  private Map<String, String> fAttributes;

  public WPCodeBlock() {
    super("{code}");
    fAttributes = null;
  }

  public void setContents(String contents) {
    this.contents = contents;
  }

  @SuppressWarnings("unchecked")
  @Override
  public void renderHTML(ITextConverter converter, Appendable buf,
      IWikiModel wikiModel) throws IOException {
    if (NEW_LINES) {
      buf.append("\n<pre");
    } else {
      buf.append("<pre");
    }
    HTMLTag.appendEscapedAttributes(buf, fAttributes);
    buf.append(">");
    String rawWikiText = Utils.ltrimNewline(contents);

    /** prevent preTag duplicate */
    @SuppressWarnings("rawtypes")
    List mockNodeList = new ArrayList();
    ContentToken token = new ContentToken(rawWikiText);
    mockNodeList.add(token);
    converter.nodesToText(mockNodeList, buf, wikiModel);

    /** regular way */
    /**
     * AbstractParser parser = wikiModel.createNewInstance(rawWikiText);
     * TagStack fStack = parser.parseRecursiveInternal(wikiModel, true, false);
     * converter.nodesToText(fStack.getNodeList(), buf, wikiModel);
     */

    buf.append("</pre>");
  }

  @Override
  public Object clone() {
    WPCodeBlock tt = (WPCodeBlock) super.clone();
    if (fAttributes == null) {
      tt.fAttributes = null;
    } else {
      tt.fAttributes = new HashMap<String, String>(fAttributes);
    }
    return tt;
  }

  @Override
  public boolean isReduceTokenStack() {
    return true;
  }

  @Override
  public String getParents() {
    return Configuration.SPECIAL_BLOCK_TAGS;
  }
}