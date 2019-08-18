package com.example.lafayetencode;

import android.Manifest;
import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static String FILE = Environment.getExternalStorageDirectory()
            + "/LafayetteSales.pdf";


    final Calendar myCalendar = Calendar.getInstance();

    EditText to_date, from_date;

    EditText item_name, item_quantity, item_price;

    Button btn_add, btn_finish;

    int total = 0;

    boolean todate = false;

    ArrayList<ItemModel> itemModels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
                     setContentView(R.layout.activity_main);

                     to_date = findViewById(R.id.to_date);
                     from_date = findViewById(R.id.from_date);
                     item_name = findViewById(R.id.item_name);
                     item_quantity = findViewById(R.id.item_quantity);
                     item_price  = findViewById(R.id.item_price);
                     btn_add = findViewById(R.id.btn_add);
                     btn_finish = findViewById(R.id.btn_finish);


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
                todate = false;
            }
        });

        from_date.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(MainActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                todate = true;
            }
        });

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ItemModel itemModel = new ItemModel();
                itemModel.setItem_name(item_name.getText().toString());
                itemModel.setItem_price(Integer.parseInt(item_price.getText().toString()));
                itemModel.setItem_quantity(Integer.parseInt(item_quantity.getText().toString()));
                itemModels.add(itemModel);
            }
        });



    }

    private void updateLabel() {
        String myFormat = "MMM dd, yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        if (!todate) {
            to_date.setText(sdf.format(myCalendar.getTime()));
        } else {
            from_date.setText(sdf.format(myCalendar.getTime()));
        }
    }

    public void createPDF(){
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

        // Creating Chunk
        Chunk mOrderDetailsTitleChunk = new Chunk("Sale Details", mOrderDetailsTitleFont);

        Paragraph mOrderDetailsTitleParagraph = new Paragraph(mOrderDetailsTitleChunk);
        mOrderDetailsTitleParagraph.setAlignment(Element.ALIGN_CENTER);

        try {
            document.add(new Chunk(lineSeparator));
            document.add(mOrderDetailsTitleParagraph);
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
        try {

            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100);
            table.addCell(getCell("Text to the left", PdfPCell.ALIGN_LEFT));
            table.addCell(getCell("Text in the middle", PdfPCell.ALIGN_CENTER));
            table.addCell(getCell("Text to the right", PdfPCell.ALIGN_RIGHT));
            document.add(table);
          //  document.add(new Paragraph("TEST"));
       /*     document.add(new Chunk(lineSeparator));
            document.add(new Paragraph("TEST2"));
*/
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        document.close();


    }

    public PdfPCell getCell(String text, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text));
        cell.setPadding(0);
        cell.setHorizontalAlignment(alignment);
        cell.setBorder(PdfPCell.NO_BORDER);
        return cell;
    }
}
