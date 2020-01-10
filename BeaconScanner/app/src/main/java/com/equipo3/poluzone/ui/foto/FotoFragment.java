package com.equipo3.poluzone.ui.foto;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.equipo3.poluzone.NavigationDrawerActivity;
import com.equipo3.poluzone.R;
import com.leinardi.android.speeddial.SpeedDialView;

import java.io.IOException;

import static android.app.Activity.RESULT_OK;

public class FotoFragment extends Fragment {
    //---------------------------------------------------------------------------
    //Clase relacionada con el botón Polufoto
    //---------------------------------------------------------------------------
    private final int PEDIR_1_PERMISO=1;
    private final int PEDIR_2_PERMISOS=2;
    private ImageView foto;
    private ImageButton botonCamera;
    private SpeedDialView speedDialView;
    public NavigationDrawerActivity navigationDrawerActivity;

    //---------------------------------------------------------------------------
    // Metodo llamado al crear la actividad
    //---------------------------------------------------------------------------
    public View onCreateView(@NonNull LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_foto, container, false);
        
        // acceder speed dial para esconderlo
        speedDialView = getParentFragment().getActivity().findViewById(R.id.fab);
        speedDialView.hide();

        botonCamera= root.findViewById(R.id.foto_button);
        foto = root.findViewById(R.id.imagen);

        // Para no tener que darle al boton de la camara para que esta se abra:
        abrirCamara();

        // Escuchador del boton que abre la cámara
        botonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("pruebas", "Abrir camara");
                abrirCamara();
            }
        });
        navigationDrawerActivity = (NavigationDrawerActivity) getParentFragment().getActivity();
      //  navigationDrawerActivity.servidorFake.callback = this;

        return root;
    }

    /*@Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);


    }*/

    private void abrirCamara(){
        // String con los permisos necesarios
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA};

        // Ifs que dependen de si la aplicacion tiene ya los permisos necesarios
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {// Marshmallow+
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {// Si no hay ningun permiso
                requestPermissions(perms,
                        PEDIR_2_PERMISOS);
            }else {

                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        // Show an expanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.
                        // No se necesita dar una explicación al usuario, sólo pedimos el permiso.
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                PEDIR_1_PERMISO);
                    }
                } else { //have permissions
                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        // Should we show an explanation?
                        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CAMERA)) {
                            // Show an expanation to the user *asynchronously* -- don't block
                            // this thread waiting for the user's response! After the user
                            // sees the explanation, try again to request the permission.
                            // No se necesita dar una explicación al usuario, sólo pedimos el permiso.
                            requestPermissions(new String[]{Manifest.permission.CAMERA},
                                    PEDIR_1_PERMISO);
                            // MY_PERMISSIONS_REQUEST_CAMARA es una constante definida en la app. El método callback obtiene el resultado de la petición.
                        }
                    } else { //have permissions
                        Log.d("hola", "3");
                        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(i, 0);
                    }
                }

            }
        } else { // Pre-Marshmallow
            Log.d("hola", "4");
            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(i, 0);
        }
        // /IFs
    }


    //------------------------------------------------------------------------------
    // Se llama despues de tomar una foto para mostrar la imagen en el ImageView
    //------------------------------------------------------------------------------
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

            if (resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                foto.setImageBitmap(imageBitmap);
                try {
                    navigationDrawerActivity.servidorFake.insertarImagen(imageBitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
    }

    //------------------------------------------------------------------------------
    // Se llama despues de dar o denegar los permisos
    //------------------------------------------------------------------------------
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Log.d("hola","si");
        switch (requestCode) {
            case PEDIR_1_PERMISO : {// En el caso  de que la aplicacion haya solicitado aceptar un permiso
                // Si la petición es cancelada, el array resultante estará vacío.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {//En el caso de haberlo permitido
                    // El permiso ha sido concedido.
                    Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(i,0);
                } else {
                    // Permiso denegado, deshabilita la funcionalidad que depende de este permiso.
                }
                return;
            }
            case PEDIR_2_PERMISOS : {// En el caso  de que la aplicacion haya solicitado aceptar dos permisos
                // Si la petición es cancelada, el array resultante estará vacío.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {//En el caso de haberlos permitido
                    // El permiso ha sido concedido.
                    Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(i,0);
                } else {
                    // Permiso denegado, deshabilita la funcionalidad que depende de este permiso.
                }
                return;
            }
        }
    }

}