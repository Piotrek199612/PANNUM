package com.example.kjankiewicz.android_07c01;


import android.app.Activity;
import android.database.Cursor;
import android.provider.CallLog;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Field;

@RunWith(AndroidJUnit4.class)
public class ApplicationMyMainActivityTest extends ActivityInstrumentationTestCase2<MyMainActivity> {

    private Solo solo;

    private Activity activity;

    public ApplicationMyMainActivityTest() {
        super(MyMainActivity.class);
    }

    /**
     * Czy (w implementacji klienta dostawcy treści CallLog) przypisano poprawną wartość
     * do zmiennej mSelection - czyli czy zdefiniowano poprawnie sposób selekcji informacji
     * ograniczającej dane tylko do połączeń przychodzących?
     */
    @Test
    public void checkMSelectionValue() throws InterruptedException, NoSuchFieldException, IllegalAccessException {
        Field f = activity.getClass().getDeclaredField("mSelection");
        f.setAccessible(true);
        String mSelection = (String) f.get(activity);
        assertEquals(mSelection.trim().toLowerCase(), (CallLog.Calls.TYPE + " = ? ").trim().toLowerCase());
    }

    /**
     * Czy (w implementacji klienta dostawcy treści CallLog) przypisano poprawną wartość do
     * zmiennej mOrderBy - czyli czy zdefiniowano poprawną kolejność pobieranych informacji?
     */
    @Test
    public void checkMOrderByValue() throws NoSuchFieldException, IllegalAccessException {
        Field f = activity.getClass().getDeclaredField("mOrderBy");
        f.setAccessible(true);
        String mSelection = (String) f.get(activity);
        assertEquals(mSelection.trim().toLowerCase(), (CallLog.Calls.DATE + " DESC").toLowerCase());
    }

    /**
     * Czy (w implementacji klienta dostawcy treści CallLog) w sposób właściwy wykonano zapytanie
     * do dostawcy treści CallLog (użycie metody ContentResolver.query() i przypisanie wyniku do kursora)?
     */
    @Test
    public void checkCursorByValue() throws IllegalAccessException, NoSuchFieldException {
        Field callsCursorField = activity.getClass().getDeclaredField("callsCursor");
        callsCursorField.setAccessible(true);
        Cursor callsCursor = (Cursor) callsCursorField.get(activity);

        Field mProjectionField = activity.getClass().getDeclaredField("mProjection");
        mProjectionField.setAccessible(true);
        String[] columnNames = (String[]) mProjectionField.get(activity);

        assertNotNull(callsCursor);
        assertEquals(callsCursor.getColumnCount(), 5);
        for (int i = 0; i < columnNames.length; i++) {
            assertEquals(callsCursor.getColumnNames()[i], columnNames[i]);
        }
    }

    /**
     *  Czy (w implementacji klienta dostawcy treści Browser) przypisano poprawną wartość do zmiennej
     *  callsListItems? Sprawdź kompatybilność zmiennej callsListItems ze zmienną callsListColumns.
     *  Sprawdź czy wartości w zmiennej callsListItems odwołują się do właściwych zasobów.
     */
    @Test
    public void checkCallLogListItems() throws NoSuchFieldException, IllegalAccessException {
        Field f = activity.getClass().getDeclaredField("callsListItems");
        f.setAccessible(true);
        int[] callsListItems = (int[]) f.get(activity);
        assertEquals(callsListItems.length, 2);
        assertEquals(callsListItems[0], R.id.callNumberView);
        assertEquals(callsListItems[1], R.id.callDateView);
    }

    /**
     * Czy (w implementacji klienta dostawcy treści CallLog) poprawnie został utworzony obiekt
     * adaptera callsLogCursorAdapter - czy wykorzystana metoda daje szanse na poprawną wizualizację
     * danych pobranych od dostawcy treści?
     */
    @Test
    public void checkCallLogCursorAdapter() throws NoSuchFieldException, IllegalAccessException {
        Field f = activity.getClass().getDeclaredField("callsCursorAdapter");
        f.setAccessible(true);
        SimpleCursorAdapter callsCursorAdapter = (SimpleCursorAdapter) f.get(activity);

        Field cursorField = activity.getClass().getDeclaredField("callsCursor");
        cursorField.setAccessible(true);
        Cursor cursor = (Cursor) cursorField.get(activity);

        assertNotNull(cursor);
        assertNotNull(callsCursorAdapter);
        assertEquals(callsCursorAdapter.getCursor(), cursor);
    }

    /**
     * Czy klient dostawcy treści Browser poprawnie wyświetla dane dotyczące zakładek w głównej
     * aktywności aplikacji?
     */
    @Test
    public void checkMainActivityListView() throws NoSuchFieldException, IllegalAccessException {

        Field f = activity.getClass().getDeclaredField("callsCursor");
        f.setAccessible(true);
        Cursor cursor = (Cursor) f.get(activity);

        final ListView callsListView = (ListView) solo.getView(R.id.callsListView);
        int childCount = callsListView.getChildCount();

        assertNotNull(cursor);
        if(cursor.moveToFirst()) {
            for (int i = 0; i < childCount; i++) {
                View v = callsListView.getChildAt(i);
                TextView callNumberView = (TextView) v.findViewById(R.id.callNumberView);
                TextView callDateView = (TextView) v.findViewById(R.id.callDateView);
                String number = callNumberView.getText().toString();
                String date = callDateView.getText().toString();
                String cursorNumber = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
                String cursorDate = cursor.getString(cursor.getColumnIndex(CallLog.Calls.DATE));
                cursor.moveToNext();
                assertNotNull(number);
                assertNotNull(cursorNumber);
                assertNotNull(date);
                assertNotNull(cursorDate);
                assertEquals(number, cursorNumber);
                assertEquals(date, cursorDate);
            }
        }
        cursor.close();
    }


    @Before
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        activity = getActivity();
        solo = new Solo(getInstrumentation(), activity);
    }

    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
        super.tearDown();
    }


}
