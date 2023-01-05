package com.example.crud_livros;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    EditText etId, etNome, etAutor, etEditora;
    ListView lvLivros;
    ArrayList<Livro> arrayLivros;
    ArrayAdapter<Livro> adapterLivros;

    private String HOST = "http://192.168.0.199/crud_livros";

    private int iClicado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recuperaInstancia();
        listaLivros();
    }

    private void listaLivros() {
        String url = HOST + "/read.php";
        Ion.with(MainActivity.this).load(url).asJsonArray().setCallback(new FutureCallback<JsonArray>() {
            @Override
            public void onCompleted(Exception e, JsonArray result) {
                for(int i = 0; i < result.size(); i++) {
                    JsonObject jsonObject = result.get(i).getAsJsonObject();

                    Livro livro = new Livro();

                    livro.setId(jsonObject.get("id").getAsInt());
                    livro.setNome(jsonObject.get("nome").getAsString());
                    livro.setAutor(jsonObject.get("autor").getAsString());
                    livro.setEditora(jsonObject.get("editora").getAsString());

                    arrayLivros.add(livro);
                }

                adapterLivros.notifyDataSetChanged();
            }
        });
    }

    private void recuperaInstancia() {

        etNome = findViewById(R.id.et_nome);
        etAutor = findViewById(R.id.et_autor);
        etEditora = findViewById(R.id.et_editora);
        etId = findViewById(R.id.et_id);

        lvLivros = findViewById(R.id.lv_livros);

        arrayLivros = new ArrayList<>();
    adapterLivros = new ArrayAdapter<>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, arrayLivros);

        lvLivros.setAdapter(adapterLivros);
        lvLivros.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            etId.setText(String.valueOf(arrayLivros.get(i).getId()));
            etNome.setText(arrayLivros.get(i).getNome());
            etAutor.setText(arrayLivros.get(i).getAutor());
            etEditora.setText(arrayLivros.get(i).getEditora());

            iClicado = i;
        }
    });
}

    public void adicionar(View v) {
        String nome = etNome.getText().toString();
        String autor = etAutor.getText().toString();
        String editora = etEditora.getText().toString();
        String id = etId.getText().toString();

        if(nome.isEmpty()) {
            etNome.setError("Insira um nome!");
        } else {
            if(id.isEmpty()){
                String url = HOST + "/create.php";
                Ion.with(MainActivity.this)
                        .load(url)
                        .setBodyParameter("nome", nome)
                        .setBodyParameter("autor", autor)
                        .setBodyParameter("editora", editora)
                        .asJsonObject()
                        .setCallback(new FutureCallback<JsonObject>() {
                            @Override
                            public void onCompleted(Exception e, JsonObject result) {
                                if(result.get("CREATE").getAsString().equals("OK")){
                                    int idRet = Integer.parseInt(result.get("ID").getAsString());

                                    Livro livro = new Livro();
                                    livro.setId(idRet);
                                    livro.setNome(nome);
                                    livro.setAutor(autor);
                                    livro.setEditora(editora);
                                    arrayLivros.add(livro);
                                    adapterLivros.notifyDataSetChanged();


                                    Toast.makeText(MainActivity.this, "Adicionado com sucesso! ID: " + idRet, Toast.LENGTH_LONG).show();
                                }else {
                                    Toast.makeText(MainActivity.this, "Erro ao adicionar!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                limpaCampos();
            }
        }
    }


    public void excluir(View view) {
        String id = etId.getText().toString();
        String url = HOST + "/delete.php";

        Ion.with(MainActivity.this).load(url).setBodyParameter("idDel", id).asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if(result.get("DELETE").getAsString().equals("OK")){
                            Livro livro = new Livro();
                            livro.setId(Integer.parseInt(id));

                            arrayLivros.remove(iClicado);
                            adapterLivros.notifyDataSetChanged();

                            Toast.makeText(MainActivity.this, "Excluído com sucesso!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Erro ao excluir!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        limpaCampos();

    }

    private void limpaCampos() {
        etNome.setText("");
        etAutor.setText("");
        etEditora.setText("");
        etNome.requestFocus();
    }

    public void atualizar(View view) {
        String nome = etNome.getText().toString();
        String autor = etAutor.getText().toString();
        String editora = etEditora.getText().toString();
        String id = etId.getText().toString();
        String url = HOST + "/update.php";
        Ion.with(MainActivity.this)
                .load(url)
                .setBodyParameter("id", id)
                .setBodyParameter("nome", nome)
                .setBodyParameter("autor", autor)
                .setBodyParameter("editora", editora)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if(result.get("UPDATE").getAsString().equals("OK")){
                            Livro livro = new Livro();
                            livro.setId(Integer.parseInt(id));
                            livro.setNome(nome);
                            livro.setAutor(autor);
                            livro.setEditora(editora);
                            arrayLivros.set(iClicado, livro);
                            adapterLivros.notifyDataSetChanged();

                            Toast.makeText(MainActivity.this, "Atualizado com sucesso!", Toast.LENGTH_LONG).show();
                        }else {
                            Toast.makeText(MainActivity.this, "Erro ao atualizar!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        limpaCampos();
    }
}

