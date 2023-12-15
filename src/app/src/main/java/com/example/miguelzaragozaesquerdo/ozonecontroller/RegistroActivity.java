package com.example.miguelzaragozaesquerdo.ozonecontroller;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * La clase RegistroActivity gestiona el proceso de registro de usuarios en la aplicación.
 * Proporciona una interfaz de usuario interactiva para recopilar información esencial del usuario,
 * como nombre, número de teléfono y verificación de código. Utiliza animaciones para guiar al usuario
 * a través de cada paso del registro, culminando con la opción de enlazar un sensor mediante el escaneo
 * de un código QR. Los datos recopilados se envían a la actividad ScanActivity para su procesamiento adicional.
 * Esta clase extiende AppCompatActivity y se infla con el diseño definido en R.layout.registro_activity.
 *
 * @author Mario Merenciano
 */
public class RegistroActivity extends AppCompatActivity {

    // Declaro todas las variables que tenemos en el xml
    public TextView textoBienvenida;
    public TextView textoVamosAEmpezar;
    public TextView textoIntroduceTuNombre;
    public EditText inputNombre;
    public TextView textoGenial;
    public TextView textoIntroduceTuTelefono;
    public EditText inputTelefono;
    public TextView textoEnlazaSensor;
    public Button btnGuardarNombre;
    public Button btnGuardarTelefono;
    private Button btnFinalizarRegistro;

    // DATOS DEL USUARIO
    private String nombre;
    private String email;
    private String password;
    private String telefono;
    private String codigoSensor;


    // CODIGO
    private TextView textoCodigo;
    private EditText inputCodigo;
    private Button btnContinuarCodigo;
    private Button btnReenviarCodigo;
    private Button btnVolverCodigo;
    private String codigoVerificacionRegistro;

    // OBJETO USUARIO
    private DatosUsuario datosUsuario;


    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro_activity);

        datosUsuario = new DatosUsuario();

        Intent intent = getIntent();
        codigoVerificacionRegistro = (String) intent.getSerializableExtra("codigoVerificacionRegistro");
        email = (String) intent.getSerializableExtra("email");
        password = (String) intent.getSerializableExtra("password");

        // Variables para los degradados y la animación
        // Declaro los fadeIn y fadeOut para que el texto no aparezca bruscamente
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
        fadeIn.setDuration(200);
        fadeIn.setFillBefore(true);
        fadeIn.setFillAfter(true);

        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator()); //and this
        fadeOut.setDuration(1000);
        fadeOut.setFillAfter(true);

        AnimationSet animationIn = new AnimationSet(false);
        animationIn.addAnimation(fadeIn);

        AnimationSet animationOut = new AnimationSet(true);
        animationOut.addAnimation(fadeOut);

        // Enlazo todas las variables creadas con las declaraciones del xml
        textoBienvenida = findViewById(R.id.texto_bienvenido);
        textoVamosAEmpezar = findViewById(R.id.texto_vamos_a_empezar);
        textoIntroduceTuNombre = findViewById(R.id.texto_introduce_tu_nombre);
        inputNombre = findViewById(R.id.input_nombre);
        textoGenial = findViewById(R.id.texto_genial);
        textoIntroduceTuTelefono = findViewById(R.id.texto_introduce_tu_numero_de_telefono);
        inputTelefono = findViewById(R.id.input_telefono);
        textoEnlazaSensor = findViewById(R.id.texto_enlaza_sensor);
        btnGuardarNombre = findViewById(R.id.btnNombre);
        btnGuardarTelefono = findViewById(R.id.btnTelefono);
        btnFinalizarRegistro = findViewById(R.id.btnFinRegistro);

        // CODIGO
        inputCodigo = findViewById(R.id.input_CodigoVerificar);
        textoCodigo = findViewById(R.id.texto_CodigoVerificar);
        btnContinuarCodigo = findViewById(R.id.btnContinuarCodigoVerificar);
        btnVolverCodigo = findViewById(R.id.btnVolverCodigoVerificar);
        btnReenviarCodigo = findViewById(R.id.btnReenviarCodigoVerificar);

        //Creo una lista con todos los text views
        List<TextView> elementosTextView = new ArrayList<>(Arrays.asList(
                textoBienvenida,
                textoVamosAEmpezar,
                textoIntroduceTuNombre,
                textoGenial,
                textoIntroduceTuTelefono,
                textoEnlazaSensor,
                textoCodigo
        ));

        // Creo una lista con todos los botones
        List<Button> elementosButtons = new ArrayList<>(Arrays.asList(
                btnGuardarNombre,
                btnGuardarTelefono,
                btnContinuarCodigo,
                btnReenviarCodigo,
                btnVolverCodigo,
                btnFinalizarRegistro
        ));

        // Creo una lista con todos los edit text
        List<EditText> elementosEditText = new ArrayList<>(Arrays.asList(
                inputNombre,
                inputTelefono,
                inputCodigo
        ));

        // Coloco en la parte de abajo todos los elementos dependiendo de su tipo
        colocarTextViewsEnlaParteDeAbajo(elementosTextView);
        colocarButtonsEnlaParteDeAbajo(elementosButtons);
        colocarEditTextEnLaParteDeAbajo(elementosEditText);

        // Inicio el proceso de registro
        animacionCodigoVerificacion();
    }

    /**
     * Inicia una secuencia de animaciones para la bienvenida del usuario.
     * Esta función maneja una serie de animaciones que se ejecutan con retrasos específicos.
     * Inicialmente, muestra un texto de bienvenida. Luego, reemplaza este texto por una invitación
     * para que el usuario introduzca su nombre y finalmente muestra un campo de texto y un botón
     * para guardar el nombre del usuario.
     * Las animaciones se realizan mediante el uso de múltiples instancias de Handler y postDelayed.
     */
    private void animacionBienvenidaNombre(){
        Handler A = new Handler();
        A.postDelayed(new Runnable() {
            @Override
            public void run() {
                mostrarTexto(textoBienvenida, 0);
            }
        }, 1000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                despedirTexto(textoBienvenida);
                mostrarTexto(textoIntroduceTuNombre, -200);
            }
        }, 3000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mostrarEditText(inputNombre, 0);
                mostrarButton(btnGuardarNombre, 300);
            }
        }, 5500);
    }

    /**
     * Maneja el evento de clic en el botón para guardar el nombre del usuario.
     * Este método verifica si el campo de texto 'inputNombre' no está vacío. Si contiene un nombre,
     * almacena este nombre en la variable 'nombre', luego procede a animar la desaparición del texto instructivo,
     * del campo de texto y del botón de guardado. Después de ocultar estos elementos, inicia la animación
     * relacionada con la introducción del teléfono del usuario llamando a 'animacionTelefono'.
     * Este método es un paso clave en el proceso de registro del usuario, asegurando que se haya proporcionado un nombre válido.
     *
     * @param view El componente de la interfaz de usuario que desencadena este método (usualmente un botón).
     */
    public void guardarNombre(View view){
        if(!inputNombre.getText().toString().equals("")){
            nombre = inputNombre.getText().toString();
            despedirTexto(textoIntroduceTuNombre);
            despedirEditText(inputNombre);
            despedirButton(btnGuardarNombre);
            animacionTelefono();
        }
    }

    /**
     * Inicia una secuencia de animaciones para la verificación de código.
     * Esta función maneja una serie de animaciones que se ejecutan con retrasos específicos.
     * Comienza mostrando un texto para indicar al usuario que introduzca el código de verificación.
     * Después de un breve retraso, muestra un campo de texto para la entrada del código y varios botones:
     * uno para continuar después de ingresar el código, otro para reenviar el código, y un último botón para volver.
     * Estas acciones se realizan utilizando Handlers y el método postDelayed.
     */
    private void animacionCodigoVerificacion(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mostrarTexto(textoCodigo, -200);
            }
        }, 1000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mostrarEditText(inputCodigo, 0);
                mostrarButton(btnContinuarCodigo, 300);
                mostrarButton(btnReenviarCodigo, 300);
                mostrarButton(btnVolverCodigo, 300);
            }
        }, 2000);
    }

    /**
     * Verifica el código de registro introducido por el usuario.
     * Este método compara el texto introducido en el campo 'inputCodigo' con el 'codigoVerificacionRegistro' almacenado.
     * Si el código introducido coincide con el código de verificación, procede a animar la desaparición
     * del texto del código, el campo de entrada del código y los botones asociados.
     * Después de ocultar estos elementos, inicia la animación de bienvenida para el ingreso del nombre del usuario
     * mediante la llamada a 'animacionBienvenidaNombre'.
     * Este método es un paso clave en el proceso de verificación del registro del usuario.
     *
     * @param view El componente de la interfaz de usuario que desencadena este método (usualmente un botón).
     */
    public void verificarCodigo(View view){
        if(inputCodigo.getText().toString().equals(codigoVerificacionRegistro)){
            despedirTexto(textoCodigo);
            despedirEditText(inputCodigo);
            despedirButton(btnContinuarCodigo);
            despedirButton(btnVolverCodigo);
            despedirButton(btnReenviarCodigo);
            animacionBienvenidaNombre();
        }
    }

    /**
     * Maneja el evento de clic en el botón para reenviar el código de verificación.
     * Este método genera un nuevo código de verificación aleatorio y lo envía al email del usuario.
     * El nuevo código se genera utilizando 'Utilidades.codigoAleatorio()' y se envía mediante 'Utilidades.enviarConGMail',
     * incluyendo un mensaje que instruye al usuario a introducir el código recibido para completar su registro.
     * Este método se utiliza en el proceso de verificación de la cuenta del usuario, asegurándose de que el email proporcionado es válido.
     *
     * @param view El componente de la interfaz de usuario que desencadena este método (usualmente un botón).
     */
    public void reenviarCodigo(View view){
        codigoVerificacionRegistro = Utilidades.codigoAleatorio();
        Utilidades.enviarConGMail(email, "Código de registro", codigoVerificacionRegistro);
    }

    /**
     * Inicia una secuencia de animaciones para la introducción del número de teléfono.
     * Esta función gestiona una serie de animaciones que se ejecutan con retrasos específicos.
     * Comienza mostrando un texto que invita al usuario a introducir su número de teléfono.
     * Después de un breve retraso, muestra un campo de texto para que el usuario ingrese su teléfono
     * y un botón para guardar esta información.
     * Se utilizan Handlers y el método postDelayed para controlar los tiempos de ejecución de estas acciones.
     */
    private void animacionTelefono(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mostrarTexto(textoIntroduceTuTelefono, -200);
            }
        }, 1000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mostrarEditText(inputTelefono, 0);
                mostrarButton(btnGuardarTelefono, 300);
            }
        }, 2000);
    }

    /**
     * Maneja el evento de clic en el botón para guardar el número de teléfono del usuario.
     * Este método verifica si el campo de texto 'inputTelefono' no está vacío. Si contiene un número de teléfono,
     * procede a animar la desaparición del texto instructivo, del campo de texto y del botón de guardado.
     * Después de ocultar estos elementos, almacena el número de teléfono ingresado en la variable 'telefono' y
     * inicia la animación relacionada con el enlace del sensor llamando a 'animacionCodigoSensor'.
     *
     * @param view El componente de la interfaz de usuario que desencadena este método (usualmente un botón).
     */
    public void guardarTelefono(View view){
        if(!inputTelefono.getText().toString().equals("")){
            despedirTexto(textoIntroduceTuTelefono);
            despedirEditText(inputTelefono);
            despedirButton(btnGuardarTelefono);
            telefono = inputTelefono.getText().toString();
            animacionCodigoSensor();
        }
    }

    /**
     * Inicia una secuencia de animaciones relacionadas con el enlace de un sensor.
     * Esta función gestiona una serie de animaciones con retrasos específicos para guiar al usuario en el proceso de enlazar un sensor.
     * Inicialmente, muestra un texto instructivo ('textoEnlazaSensor'). Después de un retraso, inicia automáticamente
     * la actividad de escaneo de código QR para el enlace del sensor.
     * Se utilizan Handlers y el método postDelayed para controlar los tiempos de ejecución de estas acciones.
     */
    private void animacionCodigoSensor(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mostrarTexto(textoEnlazaSensor, 0);
            }
        }, 1000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                iniciarScanQR();
            }
        }, 4000);
    }

    /**
     * Inicia la actividad de escaneo de código QR.
     * Este método prepara y lanza una nueva actividad, específicamente 'ScanActivity', para realizar el escaneo de un código QR.
     * Antes de iniciar la actividad, establece los datos del usuario (como email, teléfono, nombre y contraseña)
     * en un objeto 'datosUsuario', y luego pasa este objeto a la actividad 'ScanActivity' a través de un Intent.
     * Esto permite que la siguiente actividad tenga acceso a la información relevante del usuario.
     */
    private void iniciarScanQR(){
        Intent intent = new Intent(this, ScanActivity.class);
        datosUsuario.setEmail(email);
        datosUsuario.setTelefono(telefono);
        datosUsuario.setNombre(nombre);
        datosUsuario.setPassword(password);
        intent.putExtra("datosUsuario", datosUsuario);
        startActivity(intent);
    }

    /**
     * Coloca una lista de TextViews en la parte inferior de la pantalla.
     * Este método recorre cada TextView en la lista proporcionada y modifica su posición y opacidad.
     * Cada TextView se desplaza hacia abajo en la pantalla y su opacidad se establece en cero,
     * lo que efectivamente los hace invisibles y los posiciona para futuras animaciones o ajustes en la interfaz.
     *
     * @param elementos La lista de TextViews que se modificarán.
     */
    private void colocarTextViewsEnlaParteDeAbajo(List<TextView> elementos) {
        for (int i = 0; i < elementos.size(); i++) {
            elementos.get(i).setTranslationY(1000);
            elementos.get(i).setAlpha(0.0f);
        }
    }

    /**
     * Coloca una lista de botones en la parte inferior de la pantalla.
     * Este método recorre cada botón en la lista proporcionada y modifica su posición y opacidad.
     * Cada botón se desplaza hacia abajo en la pantalla y su opacidad se establece en cero,
     * haciéndolos efectivamente invisibles y posicionándolos para futuras animaciones o ajustes en la interfaz de usuario.
     *
     * @param elementos La lista de botones que se modificarán.
     */
    private void colocarButtonsEnlaParteDeAbajo(List<Button> elementos) {
        for (int i = 0; i < elementos.size(); i++) {
            elementos.get(i).setTranslationY(1000);
            elementos.get(i).setAlpha(0.0f);
        }
    }

    /**
     * Coloca una lista de EditTexts en la parte inferior de la pantalla.
     * Este método recorre cada EditText en la lista proporcionada y modifica su posición y opacidad.
     * Cada EditText se desplaza hacia abajo en la pantalla y su opacidad se establece en cero,
     * haciéndolos efectivamente invisibles y posicionándolos para futuras animaciones o ajustes en la interfaz de usuario.
     *
     * @param elementos La lista de EditTexts que se modificarán.
     */
    private void colocarEditTextEnLaParteDeAbajo(List<EditText> elementos) {
        for (int i = 0; i < elementos.size(); i++) {
            elementos.get(i).setTranslationY(1000);
            elementos.get(i).setAlpha(0.0f);
        }
    }

    /**
     * Anima un TextView para que aparezca progresivamente en la pantalla.
     * Esta función anima el TextView proporcionado para que se desplace verticalmente y
     * aumente su opacidad de 0 a 1, creando un efecto de aparición suave. La posición vertical
     * final del TextView puede ajustarse mediante el parámetro 'variacion'.
     * La animación tiene una duración de 1000 milisegundos y utiliza un interpolador de desaceleración.
     *
     * @param elemento  El TextView que se va a animar.
     * @param variacion La variación en la posición vertical del TextView (en píxeles).
     */
    private void mostrarTexto(TextView elemento, int variacion) {
        // Hacemos que el texto aparezca progresivamente
        elemento.animate().translationY(0 + variacion).setDuration(1000);
        elemento.animate().alpha(1).setDuration(1000).setInterpolator(new DecelerateInterpolator()).start();
    }

    /**
     * Anima un TextView para que desaparezca progresivamente de la pantalla.
     * Esta función anima el TextView proporcionado para que se desplace verticalmente hacia arriba
     * y disminuya su opacidad de 1 a 0, creando un efecto de desvanecimiento y desplazamiento.
     * La animación tiene una duración de 1000 milisegundos y utiliza un interpolador de aceleración,
     * dando la sensación de que el texto se "acelera" a medida que desaparece.
     *
     * @param elemento El TextView que se va a animar.
     */
    private void despedirTexto(TextView elemento) {
        elemento.animate().translationY(-1200).setDuration(1000);
        elemento.animate().alpha(0).setDuration(1000).setInterpolator(new AccelerateInterpolator()).start();
    }

    /**
     * Anima un EditText para que aparezca progresivamente en la pantalla.
     * Esta función anima el EditText proporcionado para que se desplace verticalmente y
     * aumente su opacidad de 0 a 1, creando un efecto de aparición suave. La posición vertical
     * final del EditText puede ajustarse mediante el parámetro 'variacion'.
     * La animación tiene una duración de 1000 milisegundos y utiliza un interpolador de desaceleración,
     * dando la sensación de una transición suave y natural.
     *
     * @param elemento  El EditText que se va a animar.
     * @param variacion La variación en la posición vertical del EditText (en píxeles).
     */
    private void mostrarEditText(EditText elemento, int variacion) {
        // Hacemos que el texto aparezca progresivamente
        elemento.animate().translationY(0 + variacion).setDuration(1000);
        elemento.animate().alpha(1).setDuration(1000).setInterpolator(new DecelerateInterpolator()).start();
    }

    /**
     * Anima un EditText para que desaparezca progresivamente de la pantalla.
     * Esta función anima el EditText proporcionado para que se desplace verticalmente hacia arriba
     * y disminuya su opacidad de 1 a 0, creando un efecto de desvanecimiento y desplazamiento.
     * La animación tiene una duración de 1000 milisegundos y utiliza un interpolador de aceleración,
     * dando la sensación de que el texto se "acelera" a medida que desaparece.
     *
     * @param elemento El EditText que se va a animar.
     */
    private void despedirEditText(EditText elemento) {
        elemento.animate().translationY(-1200).setDuration(1000);
        elemento.animate().alpha(0).setDuration(1000).setInterpolator(new AccelerateInterpolator()).start();
    }

    /**
     * Anima un Button para que aparezca progresivamente en la pantalla.
     * Esta función anima el Button proporcionado para que se desplace verticalmente y
     * aumente su opacidad de 0 a 1, creando un efecto de aparición suave. La posición vertical
     * final del Button puede ajustarse mediante el parámetro 'variacion'.
     * La animación tiene una duración de 1000 milisegundos y utiliza un interpolador de desaceleración.
     *
     * @param elemento  El Button que se va a animar.
     * @param variacion La variación en la posición vertical del Button (en píxeles).
     */
    private void mostrarButton(Button elemento, int variacion) {
        // Hacemos que el texto aparezca progresivamente
        elemento.animate().translationY(0 + variacion).setDuration(1000);
        elemento.animate().alpha(1).setDuration(1000).setInterpolator(new DecelerateInterpolator()).start();
    }

    /**
     * Anima un Button para que desaparezca progresivamente de la pantalla.
     * Esta función anima el Button proporcionado para que se desplace verticalmente hacia arriba
     * y disminuya su opacidad de 1 a 0, creando un efecto de desvanecimiento y desplazamiento.
     * La animación tiene una duración de 1000 milisegundos y utiliza un interpolador de aceleración,
     * dando la sensación de que el botón se "acelera" a medida que desaparece.
     *
     * @param elemento El Button que se va a animar.
     */
    private void despedirButton(Button elemento) {
        elemento.animate().translationY(-1200).setDuration(1000);
        elemento.animate().alpha(0).setDuration(1000).setInterpolator(new AccelerateInterpolator()).start();
    }
}