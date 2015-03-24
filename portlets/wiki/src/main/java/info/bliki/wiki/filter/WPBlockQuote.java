package info.bliki.wiki.filter;

import info.bliki.htmlcleaner.Utils;
import info.bliki.wiki.model.Configuration;
import info.bliki.wiki.model.IWikiModel;
import info.bliki.wiki.tags.HTMLTag;
import info.bliki.wiki.tags.WPTag;
import info.bliki.wiki.tags.util.TagStack;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class WPBlockQuote extends WPTag {

  private String contents = "";

  private Map<String, String> fAttributes;

  public WPBlockQuote() {
    super("{quote}");
    fAttributes = null;
  }

  public void setContents(String contents) {
    this.contents = contents;
  }

  @Override
  public void renderHTML(ITextConverter converter, Appendable buf,
      IWikiModel wikiModel) throws IOException {
    if (NEW_LINES) {
      buf.append("\n<blockquote");
    } else {
      buf.append("<blockquote");
    }
    HTMLTag.appendEscapedAttributes(buf, fAttributes);
    buf.append(">");
    String rawWikiText = Utils.ltrimNewline(contents);
    AbstractParser parser = wikiModel.createNewInstance(rawWikiText);
    TagStack fStack = parser.parseRecursiveInternal(wikiModel, true, false);
    converter.nodesToText(fStack.getNodeList(), buf, wikiModel);
    buf.append("</blockquote>");
  }

  @Override
  public Object clone() {
    WPBlockQuote tt = (WPBlockQuote) super.clone();
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