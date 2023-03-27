# Grupo11-P1
Repositório do projecto 1 de Programação avançada, do grupo 11

Caso ocorram erros envolvendo caminhos, deve alterar os caminhos nos seguintes métodos: 
WriteLog() do clientThread, colocar o caminho absoluto no fileWriter do server.log
getProperties() do ServerConfigReader, colocar no FileInputStream o caminho absoluto do server.config
leitorUltimaLinha() do ClientThreadTest (ficheiro de teste do client), colocar o caminho absoluto do server.log no FileReader

Existem ainda outros caminhos que não geraram quaisqueres problemas em nenhum momento, mas, caso acabem por gerar um erro, as suas localizações são : 
main do server
run do serverThread
