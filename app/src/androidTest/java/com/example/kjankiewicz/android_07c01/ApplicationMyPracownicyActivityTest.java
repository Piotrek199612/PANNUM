package com.example.kjankiewicz.android_07c01;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.google.common.collect.Ordering;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotEquals;

@RunWith(AndroidJUnit4.class)
public class ApplicationMyPracownicyActivityTest extends ActivityInstrumentationTestCase2<MyPracownicyActivity> {

    private Solo solo;

    private MyPracownicyActivity activity;

    private Context context;

    private PackageManager packageManager;

    public ApplicationMyPracownicyActivityTest() {
        super(MyPracownicyActivity.class);
    }

    private String[] columns = {
            PracZespEtatContract.Pracownicy.Companion.getCOLUMN_NAME_ID_PRAC(),
            PracZespEtatContract.Pracownicy.Companion.getCOLUMN_NAME_NAZWISKO(),
            PracZespEtatContract.Pracownicy.Companion.getCOLUMN_NAME_ETAT(),
            PracZespEtatContract.Pracownicy.Companion.getCOLUMN_NAME_ID_SZEFA(),
            PracZespEtatContract.Pracownicy.Companion.getCOLUMN_NAME_ID_SZEFA(),
            PracZespEtatContract.Pracownicy.Companion.getCOLUMN_NAME_PLACA_POD(),
            PracZespEtatContract.Pracownicy.Companion.getCOLUMN_NAME_PLACA_DOD(),
            PracZespEtatContract.Pracownicy.Companion.getCOLUMN_NAME_ID_ZESP()
    };

    private Integer[] idPracTab = {100, 110, 120, 130, 140, 150, 160, 170, 190, 180, 200, 210, 220, 230};
    private String[] nazwiskoTab = {"WEGLARZ", "BLAZEWICZ", "SLOWINSKI", "BRZEZINSKI", "MORZY", "KROLIKOWSKI", "KOSZLAJDA", "JEZIERSKI", "MATYSIAK", "MAREK", "ZAKRZEWICZ", "BIALY", "KONOPKA", "HAPKE"};
    private String[] etatTab = {"DYREKTOR", "PROFESOR", "PROFESOR", "PROFESOR", "PROFESOR", "ADIUNKT", "ADIUNKT", "ASYSTENT", "ASYSTENT", "SEKRETARKA", "STAZYSTA", "STAZYSTA", "ASYSTENT", "ASYSTENT"};
    private Integer[] idSzefaTab = {null,100,100,100,130,130,130,130,140,100,140,130,110,120};
    private String[] zatrudnionyTab = {"1968-01-01","1973-05-01","1977-09-01","1968-07-01","1975-09-15","1977-09-01","1985-03-01","1992-10-01","1993-09-01","1985-02-20","1994-07-15","1993-10-15","1993-10-01","1992-09-01"};
    private Double[] placaPodTab = {1730.00, 1350.00, 1070.00, 960.00, 830.00, 645.50, 590.00, 439.70, 371.00, 410.20, 208.00, 250.00, 480.00, 480.00};
    private Double[] placaDodTab = {420.50,210.00,null,null,105.00,null,null,80.50,null,null,null,170.60,null,90.00};
    private int[] idZespTab = {10, 40, 30, 20, 20, 20, 20, 20, 20, 10, 30, 30, 20, 30};


    private String[] pracownicyProjection = {
            PracZespEtatContract.Pracownicy._ID,
            PracZespEtatContract.Pracownicy.Companion.getCOLUMN_NAME_ID_PRAC(),
            PracZespEtatContract.Pracownicy.Companion.getCOLUMN_NAME_NAZWISKO(),
            PracZespEtatContract.Pracownicy.Companion.getCOLUMN_NAME_ETAT(),
            PracZespEtatContract.Pracownicy.Companion.getCOLUMN_NAME_ID_ZESP(),
            PracZespEtatContract.Pracownicy.Companion.getCOLUMN_NAME_PLACA_POD()
    };

    private String pracownicyOrderBy =
            PracZespEtatContract.Pracownicy.Companion.getCOLUMN_NAME_NAZWISKO() + " ASC";



    /**
     * Czy (w implementacji klienta bazy danych SQLite) instancja klasy SQLiteOpenHelper
     * została utworzona w sposób właściwy?
     */
    @Test
    public void checkInstanceOfSQLiteOpenHelper() throws NoSuchFieldException, IllegalAccessException {
        performCreateNewDatasetButtonClick();
        Field f = activity.getClass().getDeclaredField("mDbHelper");
        f.setAccessible(true);
        PracZespEtatDbHelper mDbHelper = (PracZespEtatDbHelper) f.get(activity);
        assertNotNull(mDbHelper);
        assertEquals(mDbHelper.getDatabaseName(), PracZespEtatDbHelper.Companion.getDATABASE_NAME());
        assertEquals(mDbHelper.getWritableDatabase().getVersion(), PracZespEtatDbHelper.Companion.getDATABASE_VERSION());
    }

    /**
     * Czy (w implementacji klienta bazy danych SQLite, w obsłudze przycisku "Create new dataset")
     * poprawnie otwarto bazę danych w trybie do zapisu?
     */
    @Test
    public void openDatabaseTest() {
        performCreateNewDatasetButtonClick();
        assertNotNull(activity.getDb());
        assertEquals(activity.getDb().isReadOnly(), false);
    }

    /**
     * Czy (w implementacji klienta bazy danych SQLite, w obsłudze przycisku "Create new dataset")
     * poprawnie wywołano polecenie kasujące zawartość tabeli PRACOWNICY?
     */
    @Test
    public void deleteTableTest() {
        performCreateNewDatasetButtonClick();
        assertNotSame(activity.getDeleteResult(), -1);
    }

    /**
     * Czy (w implementacji klienta bazy danych SQLite, w obsłudze przycisku "Create new dataset")
     * poprawnie załadowano "początkowe" dane do tabeli PRACOWNICY?
     */
    @Test
    public void insertTest() {
        performCreateNewDatasetButtonClick();
        PracZespEtatDbHelper mDbHelper = new PracZespEtatDbHelper(context);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(PracZespEtatContract.Pracownicy.Companion.getTABLE_NAME(), columns, null, null, null, null, null);
        } catch (Exception e) {}

        assertNotNull(cursor);
        assertNotSame(idPracTab.length, 0);
        assertNotSame(cursor.getCount(), 0);
        if(cursor.moveToFirst()) {
            for(int i = 0; i < idPracTab.length; i++) {
                assertEquals(cursor.getInt(0), idPracTab[i].intValue());
                assertEquals(cursor.getString(1), nazwiskoTab[i]);
                assertEquals(cursor.getString(2), etatTab[i]);
                assertEquals(cursor.getDouble(5), placaPodTab[i]);
                assertEquals(cursor.getDouble(6), placaDodTab[i] != null ? placaDodTab[i] : 0.0);
                assertSame(cursor.getInt(7), idZespTab[i]);
                cursor.moveToNext();
            }
        }
        cursor.close();
    }

    /**
     * Czy (w implementacji klienta bazy danych SQLite, w obsłudze przycisku "Create new dataset")
     * poprawnie zamknięto bazę danych?
     */
    @Test
    public void closeDatabaseTest() {
        performCreateNewDatasetButtonClick();
        assertNotNull(activity.getDb());
        assertEquals(activity.getDb().isOpen(), false);
    }

    /**
     * Czy (w implementacji klienta bazy danych SQLite) poprawnie zaimplementowano wyświetlenie danych
     * z tabeli PRACOWNICY? Sprawdź definicję plików zasobów odpowiedzialnych za wyświetlanie
     * poszczególnych wierszy z kursora i jej kompatybilność ze zmienną pracownicyListItems
     */
    @Test
    public void pracownicyGridViewTest() {
        performCreateNewDatasetButtonClick();
        performRefreshButtonClick();
        PracZespEtatDbHelper mDbHelper = new PracZespEtatDbHelper(context);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        Cursor cursor = db.query(PracZespEtatContract.Pracownicy.Companion.getTABLE_NAME(), columns, null, null, null, null, pracownicyOrderBy);

        final GridView pracownicyGridView = (GridView) solo.getView(R.id.pracownicyGridView);
        int childCount = pracownicyGridView.getChildCount();

        assertNotNull(cursor);
        assertNotSame(childCount, 0);
        if(cursor.moveToFirst()) {
            for (int i = 0; i < childCount; i++) {
                View v = pracownicyGridView.getChildAt(i);
                /*
                  nazwiskoTextView oraz etatTextView musi być zaimplementowane aby uruchomić test
                 */
                TextView naziwskoTextView = (TextView) v.findViewById(R.id.nazwiskoTextView);
                TextView etatTextView = (TextView) v.findViewById(R.id.etatTextView);
                String nazwisko = naziwskoTextView.getText().toString();
                String etat = etatTextView.getText().toString();
                String cursorNazwisko = cursor.getString(cursor.getColumnIndex(PracZespEtatContract.Pracownicy.Companion.getCOLUMN_NAME_NAZWISKO()));
                String cursorEtat = cursor.getString(cursor.getColumnIndex(PracZespEtatContract.Pracownicy.Companion.getCOLUMN_NAME_ETAT()));
                cursor.moveToNext();
                assertEquals(nazwisko, cursorNazwisko);
                assertEquals(etat, cursorEtat);
            }
        }
        cursor.close();
    }

    /**
     * Czy w implementacji własnego dostawcy treści poprawnie zaimplementowano metody:
     * delete, getType, insert, query i update?
     * Za każdą poprawnie zaimplementowaną metodę daj 1 punkt.
     * Sprawdź czy poprawnie użyto stałe klasy kontraktu - jeśli nie odejmij 1 punkt.
     */
    @Test
    public void deleteMethodTest() {
        performCreateNewDatasetButtonClick();
        int result = 0;
        try {
            result = activity.getContentResolver().delete(PracZespEtatContract.Pracownicy.Companion.getCONTENT_URI(), null, null);
        } catch (Exception e) {}

        PracZespEtatDbHelper mDbHelper = new PracZespEtatDbHelper(context);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        Cursor cursor = db.query(PracZespEtatContract.Pracownicy.Companion.getTABLE_NAME(), columns, null, null, null, null, null);
        assertNotNull(cursor);
        assertNotSame(result, 0);
        assertEquals(isCursorEmpty(cursor), true);
    }

    @Test
    public void getTypeMethodTest() {
        String result = activity.getContentResolver().getType(PracZespEtatContract.Pracownicy.Companion.getCONTENT_URI());
        String expectedResult = "vnd.android.cursor.dir/vnd.com.example.kjankiewicz.android_07c01.praczespetat." +
                PracZespEtatContract.Pracownicy.Companion.getTABLE_NAME();
        assertEquals(result, expectedResult);
    }

    @Test
    public void queryMethodTest() {
        performCreateNewDatasetButtonClick();
        Cursor cursor = null;
        try {
            cursor = activity.getContentResolver().query(PracZespEtatContract.Pracownicy.Companion.getCONTENT_URI(),
                    pracownicyProjection, null, null, pracownicyOrderBy);
        } catch(Exception e) {}
        List<String> surnames = new ArrayList<String>();

        assertNotNull(cursor);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                surnames.add(cursor.getString(cursor.getColumnIndex(PracZespEtatContract.Pracownicy.Companion.getCOLUMN_NAME_NAZWISKO())));
                cursor.moveToNext();
            }
        }
        cursor.close();
        assertTrue(Ordering.natural().isOrdered(surnames));
    }

    @Test
    public void updateMethodTest() {
        performCreateNewDatasetButtonClick();
        ContentValues cv = new ContentValues();
        cv.put(PracZespEtatContract.Pracownicy.Companion.getCOLUMN_NAME_PLACA_POD(), 1000.00);
        int result = 0;
        try {
            result = activity.getContentResolver().update(PracZespEtatContract.Pracownicy.Companion.getCONTENT_URI(),
                    cv, PracZespEtatContract.Pracownicy.Companion.getCOLUMN_NAME_NAZWISKO() +"='KROLIKOWSKI'", null);
        } catch(Exception e) {}

        String[] placaPodProjection = {
                PracZespEtatContract.Pracownicy.Companion.getCOLUMN_NAME_PLACA_POD(),
                PracZespEtatContract.Pracownicy.Companion.getCOLUMN_NAME_NAZWISKO()
        };
        String whereClause = PracZespEtatContract.Pracownicy.Companion.getCOLUMN_NAME_NAZWISKO() + " = ?";
        Cursor cursor = null;
        try {
            cursor = activity.getContentResolver().query(PracZespEtatContract.Pracownicy.Companion.getCONTENT_URI(),
                    placaPodProjection, whereClause,
                    new String[]{"KROLIKOWSKI"}, null);
        } catch(Exception e) {}

        assertNotNull(cursor);
        cursor.moveToFirst();
        String nazwisko = cursor.getString(cursor.getColumnIndex(PracZespEtatContract.Pracownicy.Companion.getCOLUMN_NAME_NAZWISKO()));
        double placaPod = cursor.getDouble(cursor.getColumnIndex(PracZespEtatContract.Pracownicy.Companion.getCOLUMN_NAME_PLACA_POD()));
        cursor.close();
        assertSame(result, 1);
        assertEquals(isCursorEmpty(cursor), false);
        assertEquals(nazwisko, "KROLIKOWSKI");
        assertEquals(placaPod, 1000.00);
    }

    /**
     * Czy implementacja przycisku "Get Count From Provider" poprawnie korzysta z zaimplementowanego
     * własnego dostawcy treści?
     */
    //@Test
    public void getCountFromProviderTest() throws NoSuchFieldException, IllegalAccessException {
        final Button getCountButton = (Button) solo.getView(R.id.getCountButton);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getCountButton.performClick();
            }
        });
        getInstrumentation().waitForIdleSync();


        String[] projection = {PracZespEtatContract.Pracownicy.Companion.getCOLUMN_NAME_ID_PRAC()};

        Field f = activity.getClass().getDeclaredField("countCursor");
        f.setAccessible(true);
        Cursor countCursor = null;
        try {
            countCursor = activity.getContentResolver().query(
                    PracZespEtatContract.Pracownicy.Companion.getCONTENT_URI(),
                    projection,
                    null,
                    null,
                    null);
        } catch(Exception e) {}
        assertNotNull(countCursor);
        assertNotEquals(activity.getCountCursor(), null);
        assertEquals(projection[0], activity.getProjection()[0]);
        assertEquals(countCursor.getCount(), activity.getCountCursor().getCount());
        assertEquals(countCursor.getColumnCount(), activity.getCountCursor().getColumnCount());
    }



    private boolean isCursorEmpty(Cursor cursor){
        if(!cursor.moveToFirst() || cursor.getCount() == 0) {
            return true;
        }
        return false;
    }


    private void performCreateNewDatasetButtonClick() {
        final Button createNewDatasetButton = (Button) solo.getView(R.id.createNewDatasetButton);
        activity.runOnUiThread(createNewDatasetButton::performClick);
        getInstrumentation().waitForIdleSync();
    }

    private void performRefreshButtonClick() {
        final Button refreshButton = (Button) solo.getView(R.id.refreshButton);
        activity.runOnUiThread(refreshButton::performClick);
        getInstrumentation().waitForIdleSync();
    }


    @Before
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        activity = getActivity();
        solo = new Solo(getInstrumentation(), activity);
        context = InstrumentationRegistry.getTargetContext();
        packageManager = getActivity().getPackageManager();
    }

    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
        super.tearDown();
    }
}
