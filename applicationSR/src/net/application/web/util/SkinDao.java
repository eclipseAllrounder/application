package net.application.web.util;


import java.util.List;
import net.application.web.entity.Skins;


public interface SkinDao {
    Integer getSizeOfSkins();
    List<Skins> getAllSkins();
}