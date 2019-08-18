package com.example.lafayetencode;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private static String FILE = Environment.getExternalStorageDirectory()
            + "/LafayetteSales.pdf";


    final Calendar myCalendar = Calendar.getInstance();

    EditText to_date, from_date;

    EditText item_name, item_quantity, item_price, item_date;

    Button btn_add, btn_finish, btn_load;

    int total = 0;

    int whichDate = 0;

    ArrayList<ItemModel> itemModels = new ArrayList<>();

    String currDate = "";

    String prevDate = "";

    TextView textview_load;

    boolean deletePdf = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
                     setContentView(R.layout.activity_main);

                     to_date = findViewById(R.id.to_date);
                     from_date = findViewById(R.id.from_date);
                     item_date = findViewById(R.id.item_date);
                     item_name = findViewById(R.id.item_name);
                     item_quantity = findViewById(R.id.item_quantity);
                     item_price  = findViewById(R.id.item_price);
                     btn_add = findViewById(R.id.btn_add);
                     btn_finish = findViewById(R.id.btn_finish);
                     btn_load = findViewById(R.id.btn_load);
                     textview_load = findViewById(R.id.textview_load);


        if (Build.VERSION.SDK_INT >= 23) {
            Dexter.withActivity(this)
                    .withPermissions(
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    ).withListener(new MultiplePermissionsListener() {
                @Override public void onPermissionsChecked(MultiplePermissionsReport report) {

                }
                @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {  }
            }).check();

        } else {
        }

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };

        to_date.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(MainActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                whichDate = 0;
            }
        });

        from_date.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(MainActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                whichDate = 1;
            }
        });

        item_date.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(MainActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                whichDate = 2;
            }
        });


        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ItemModel itemModel = new ItemModel();
                itemModel.setItem_date(item_date.getText().toString());
                itemModel.setItem_name(item_name.getText().toString());
                if (item_price.getText().toString().equals("")) {
                    Toast.makeText(MainActivity.this, "Input price", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    itemModel.setItem_price(Integer.parseInt(item_price.getText().toString()));
                }
                if (item_quantity.getText().toString().equals("")) {
                    Toast.makeText(MainActivity.this, "Input quantity", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    itemModel.setItem_quantity(Integer.parseInt(item_quantity.getText().toString()));
                }

                item_name.setText("");
                item_price.setText("");
                item_quantity.setText("");
                itemModels.add(itemModel);

                Toast.makeText(MainActivity.this, "Item successfully added", Toast.LENGTH_SHORT).show();

                SharedPreferences appSharedPrefs = PreferenceManager
                        .getDefaultSharedPreferences(MainActivity.this.getApplicationContext());
                SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
                Gson gson = new Gson();
                String json = gson.toJson(itemModels);
                prefsEditor.putString("MyObject", json);
                prefsEditor.putString("time", String.valueOf(Calendar.getInstance().getTime()));
                prefsEditor.putString("from_date", from_date.getText().toString());
                prefsEditor.putString("to_date", to_date.getText().toString());
                prefsEditor.apply();
            }
        });

        btn_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Show sale")
                        .setMessage("Are you sure you want to print this entry?")

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                createPDF();
                                total = 0;
                                prevDate = "";
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            }
        });

        btn_load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences appSharedPrefs = PreferenceManager
                        .getDefaultSharedPreferences(MainActivity.this.getApplicationContext());
                Gson gson = new Gson();
                String json = appSharedPrefs.getString("MyObject", "");
                String time = appSharedPrefs.getString("time", "");
                String from_time = appSharedPrefs.getString("from_date", "");
                String to_time = appSharedPrefs.getString("to_date", "");
                Type type = new TypeToken<ArrayList<ItemModel>>(){}.getType();


                ArrayList<ItemModel> tempItemModels = new ArrayList<>();
                tempItemModels = gson.fromJson(json, type);
                if (tempItemModels == null || tempItemModels.isEmpty()) {
                    Toast.makeText(MainActivity.this, "No saved data", Toast.LENGTH_SHORT).show();
                } else {
                    itemModels = tempItemModels;
                    prevDate = "";
                    textview_load.setText("Last Saved :" + time);
                    from_date.setText(from_time);
                    to_date.setText(to_time);
                    deletePdf = false;
                    createPDF();
                }

            }
        });



    }

    private void updateLabel() {
        String myFormat = "MMM dd, yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        if (whichDate == 0) {
            to_date.setText(sdf.format(myCalendar.getTime()));
        } else if (whichDate == 1) {
            from_date.setText(sdf.format(myCalendar.getTime()));
        }
        else if (whichDate == 2) {
            item_date.setText(sdf.format(myCalendar.getTime()));
        }
    }

    public void createPDF(){
        deletePdf = true;
        Document document = new Document();
// Location to save
        try {
            PdfWriter.getInstance(document, new FileOutputStream(FILE));
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

// Open to write
        document.open();
        document.setPageSize(PageSize.LEGAL);
        document.addCreationDate();
        document.addAuthor("lafayet order");
        document.addCreator("Kiko lawrence");

        BaseColor mColorAccent = new BaseColor(0, 153, 204, 255);
        float mHeadingFontSize = 20.0f;
        float mValueFontSize = 26.0f;

        LineSeparator lineSeparator = new LineSeparator();
        lineSeparator.setLineColor(new BaseColor(0, 0, 0, 68));

        BaseFont urName = null;
        try {
            urName = BaseFont.createFont("/res/font/brandon_medium.otf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Font mOrderDetailsTitleFont = new Font(urName, 36.0f, Font.NORMAL, BaseColor.BLACK);
        Font dateFont = new Font(urName, 14.0f, Font.NORMAL, BaseColor.BLACK);

        // Creating Chunk
        Chunk mOrderDetailsTitleChunk = new Chunk("Sale Details ", mOrderDetailsTitleFont);
        Chunk dateChunk = new Chunk("Date : " + from_date.getText().toString() +" - " +
                to_date.getText().toString() +"\n\n", dateFont);


        Paragraph mOrderDetailsTitleParagraph = new Paragraph(mOrderDetailsTitleChunk);
        mOrderDetailsTitleParagraph.setAlignment(Element.ALIGN_CENTER);

        Paragraph date = new Paragraph(dateChunk);
        date.setAlignment(Element.ALIGN_CENTER);

        try {
            document.add(mOrderDetailsTitleParagraph);
            document.add(new Paragraph("\n"));
            document.add(date);
            document.add(lineSeparator);
            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100);
            table.addCell(getCell("Item Name", PdfPCell.ALIGN_LEFT));
            table.addCell(getCell("Quantity", PdfPCell.ALIGN_CENTER));
            table.addCell(getCell("Price", PdfPCell.ALIGN_RIGHT));
            document.add(table);
            document.add(new Paragraph("\n"));
        } catch (DocumentException e) {
            e.printStackTrace();
        }
     /*   Font mOrderIdFont = new Font(urName, mHeadingFontSize, Font.NORMAL, mColorAccent);
        Chunk mOrderIdChunk = new Chunk("Order No:", mOrderIdFont);
        Paragraph mOrderIdParagraph = new Paragraph(mOrderIdChunk);
        try {
            document.add(mOrderIdParagraph);
        } catch (DocumentException e) {
            e.printStackTrace();
        }*/

     //START OF ORDER DETAILS
        if (itemModels != null && !itemModels.isEmpty()){
            for (int i = 0; i < itemModels.size() ; i++) {
        try {
            int totalPerItem = 0;
            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100);
            table.addCell(getCell(itemModels.get(i).item_name, PdfPCell.ALIGN_LEFT));
            table.addCell(getCell(String.valueOf(itemModels.get(i).item_quantity + "(" + itemModels.get(i).item_price +")" ), PdfPCell.ALIGN_CENTER));
            totalPerItem = itemModels.get(i).item_quantity * itemModels.get(i).item_price;
            table.addCell(getCell(String.valueOf(totalPerItem), PdfPCell.ALIGN_RIGHT));

            total =  total + totalPerItem;

            currDate = itemModels.get(i).getItem_date();
            if (!prevDate.equals(currDate)) {
                currDate = itemModels.get(i).getItem_date();
                document.add(new Paragraph(currDate));
                prevDate = currDate;
            }
            document.add(table);

          //  document.add(new Paragraph("TEST"));
       /*     document.add(new Chunk(lineSeparator));
            document.add(new Paragraph("TEST2"));
*/

        if (i == itemModels.size()-1){
            document.add(new Paragraph("\n"));
            document.add(lineSeparator);
            Chunk glue = new Chunk(new VerticalPositionMark());
            Paragraph p = new Paragraph("End of details");
            p.add(new Chunk(glue));
            p.add("Total Price : " + total);
            document.add(p);

        }
        } catch (DocumentException e) {
            e.printStackTrace();
        }

            }
        }

        document.close();
        if (deletePdf) {
            SharedPreferences appSharedPrefs = PreferenceManager
                    .getDefaultSharedPreferences(MainActivity.this.getApplicationContext());
        appSharedPrefs.edit().clear().apply();
        }
         openPdf();

    }

    public PdfPCell getCell(String text, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text));
        cell.setPadding(0);
        cell.setHorizontalAlignment(alignment);
        cell.setBorder(PdfPCell.NO_BORDER);
        return cell;
    }

    public void openPdf() {


        Intent intent = new Intent(Intent.ACTION_VIEW);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            File file = new File(FILE);
            Uri uri = FileProvider.getUriForFile(this,
                    this.getApplicationContext().getPackageName() + ".fileprovider", file);
            intent.setDataAndType(uri, "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        } else {
            intent.setDataAndType(Uri.parse(FILE), "application/pdf");
        }

        try {
            startActivity(intent);
        } catch (Throwable t) {
            t.printStackTrace();
            //attemptInstallViewer();
        }

    }
}
