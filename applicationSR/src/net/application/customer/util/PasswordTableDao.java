package net.application.customer.util;

import java.util.List;

import net.application.customer.entity.PasswordTable;
import net.application.web.entity.Skins;


public interface PasswordTableDao {
    Integer getSizeOfPasswords();
    List<PasswordTable> getAllPasswords();
}
