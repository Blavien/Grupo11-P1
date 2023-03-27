# Grupo11-P1
Repositório do projecto 1 de Programação avançada, do grupo 11

Ao fazer pull desta branch, é recomendando fazer update, porque podem fazer pull de uma versão desatualizada do branch.

No terminal do main dos clientes, nem sempre é possível ver o menu mas é possível inserir qualquer opção do menu e deverá funcionar.

Caso ocorram erros envolvendo caminhos, deve alterar os caminhos nos seguintes métodos: 
    WriteLog() do clientThread, colocar o caminho absoluto no fileWriter do server.log
    getProperties() do ServerConfigReader, colocar no FileInputStream o caminho absoluto do server.config
    leitorUltimaLinha() do ClientThreadTest (ficheiro de teste do client), colocar o caminho absoluto do server.log no FileReader
    IOException() e setUp() do filtroThreadTest
    

    Existem ainda outros caminhos que não geraram quaisqueres problemas em nenhum momento, mas, caso acabem por gerar um erro, as suas localizações são : 
    main do server
    run do serverThread

    Os testes unitários não passam nas github actions (apesar de passarem no IDE) porém se comentarmos os mesmos, o javaDOC é gerado

    
