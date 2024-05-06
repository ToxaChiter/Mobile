package com.example.lab_5.Fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.lab_5.Note;
import com.example.lab_5.NoteAdapter;
import com.example.lab_5.DB.DB;
import com.example.lab_5.R;

import java.util.ArrayList;
import java.util.List;

public class FragmentShow extends Fragment {
    private ListView listViewNotes;
    private NoteAdapter noteAdapter;
    private DB db;

    public static FragmentShow newInstance(int page) {
        FragmentShow fragment = new FragmentShow();
        Bundle args = new Bundle();
        args.putInt("num", page);
        fragment.setArguments(args);
        return fragment;
    }

    public FragmentShow() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_show, container, false);

        db = new DB(getActivity());
        db.open();

        listViewNotes = result.findViewById(R.id.listViewNotes);
        TextView pageHeader = result.findViewById(R.id.fragSHOW);
        String header = "Все заметки: ";
        pageHeader.setText(header);

        updateData(); // Вызываем метод обновления данных

        return result;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Закрываем подключение к базе данных при уничтожении фрагмента
        db.close();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateData(); // Вызовите метод обновления данных
    }
    private void updateData() {
        Cursor cursor = db.getAllData();
        List<Note> notes = new ArrayList<>();

        while (cursor.moveToNext()) {
            int idIndex = cursor.getColumnIndex("id");
            int descriptionIndex = cursor.getColumnIndex("description");

            if (idIndex >= 0 && descriptionIndex >= 0) {
                int id = cursor.getInt(idIndex);
                String description = cursor.getString(descriptionIndex);

                Note note = new Note(id, description);
                notes.add(note);
            }
        }

        noteAdapter = new NoteAdapter(getContext(), notes); // Используем getContext()
        listViewNotes.setAdapter(noteAdapter);

        // Закрываем курсор
        cursor.close();
    }
}