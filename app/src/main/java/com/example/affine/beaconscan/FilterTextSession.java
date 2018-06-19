package com.example.affine.beaconscan;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by lenovo on 20/11/17.
 */

public class FilterTextSession {
    private final SharedPreferences m_pref;
    private final SharedPreferences.Editor m_editor;

    public FilterTextSession(Context context) {
        m_pref = context.getSharedPreferences("filterText", 0);
        m_editor = m_pref.edit();
    }

    public void setFilterText(String filter) {
        if (filter == null) {
            return;
        }
        m_editor.putString("filter", filter);
        m_editor.commit();
    }

    public String getFilterText() {
        String filter = m_pref.getString("filter", null);
        if (Strings.isEmpty(filter)) {
            return "";
        }
        return filter;
    }

    public void clear() {
        m_editor.clear();
        m_editor.commit();
    }

    public boolean filterExist() {
        if (Strings.isEmpty(getFilterText())) {
            return false;
        }
        return true;
    }

}

