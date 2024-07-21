setlocal
cd /d %~dp0
java --module-path "3AddressCodeModule_lib/javafx-sdk-22.0.1/lib" --add-modules javafx.controls,javafx.fxml,javafx.web -jar 3AddressCodeModule.jar