package net.minecraft.src;


import farn.bookandquill.BookAndQuill;

@SuppressWarnings("unused")
public class mod_BookAndQuill extends BaseMod {

    @MLProp(name="Book And Quill Item ID", info="ID")
    public static int bookAndQuilID = 30000;

    @MLProp(name="Written Book Item ID", info="ID")
    public static int writtenBookID = 29999;

    static  {
        BookAndQuill.init();
    }

    @Override
    public String Version() {
        return BookAndQuill.VERSION;
    }

    public String Name() {
        return BookAndQuill.NAME;
    }

    public String Description() {
        return BookAndQuill.DESCRIPTION;
    }

    public String Icon() {
        return BookAndQuill.ICON_PATH;
    }
}