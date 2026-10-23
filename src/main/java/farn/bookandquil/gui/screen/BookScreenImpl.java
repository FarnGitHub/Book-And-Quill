package farn.bookandquil.gui.screen;

import farn.bookandquil.util.PageFocus;

public interface BookScreenImpl {

    int pageDisplayInScreen();

    void renderBookCover();

    void renderSigning();

    void renderBook();

    int contentXStart(PageFocus focus);

    int contentYStart(PageFocus focus);

    int contentWidth(PageFocus focus);

    void pageClick(int x, int y);

    void updateButton();
}
