import {
  OAuthProvider,
  User,
  initializeAuth,
  signInWithCredential,
  signInWithEmailAndPassword,
  signOut,
} from "firebase/auth";
import "./App.css";
// Import the functions you need from the SDKs you need
import { initializeApp } from "firebase/app";
import { useState } from "react";
// TODO: Add SDKs for Firebase products that you want to use
// https://firebase.google.com/docs/web/setup#available-libraries

// Your web app's Firebase configuration
const firebaseConfig = {
  apiKey: "AIzaSyBSoh7cJvIbXeTfYEiQ-COFVsFRI26FD7I",
  authDomain: "one-auth-project-2.firebaseapp.com",
  projectId: "one-auth-project-2",
  storageBucket: "one-auth-project-2.appspot.com",
  messagingSenderId: "447908478500",
  appId: "1:447908478500:web:903ba0c10bcbce66dba2b1",
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);
const auth = initializeAuth(app);

const EMAIL = "murilo@onepercent.io";
const PASSWORD = "somepass";

function App() {
  const [user, setUser] = useState<User>();
  const [error, setError] = useState<string>();

  if (user) {
    return (
      <>
        Authenticated as {user.email}
        <button
          onClick={() => {
            signOut(auth).then(() => setUser(undefined));
          }}
        >
          Sign out
        </button>
      </>
    );
  }

  return (
    <>
      <button
        onClick={() => {
          signInWithEmailAndPassword(auth, EMAIL, PASSWORD)
            .then((u) => {
              setUser(u.user);
            })
            .catch((e) => {
              setError(e.message);
            });
        }}
      >
        Authenticate with {EMAIL}
      </button>

      <button
        onClick={() => {
          // Pegamos de alguma forma o token do usuário do app
          const token = new URLSearchParams(location.search).get("auth_token");
          // Intanciamos o provider que criamos
          const provider = new OAuthProvider("oidc.the-client-android-app");
          // Criamos uma credencial com o token que nos foi passado
          const credential = provider.credential({
            idToken: token!,
          });
          // Executamos o signin usando a credencial/provider
          signInWithCredential(auth, credential)
            .then((u) => {
              setUser(u.user);
            })
            .catch((e) => {
              setError(e.message);
            });
        }}
      >
        Authenticate with OIDC
      </button>

      <p>Error: {error ?? "No errors 🙏 yet 👹"}</p>
    </>
  );
}

export default App;
