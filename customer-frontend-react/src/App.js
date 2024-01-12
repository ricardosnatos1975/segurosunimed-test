import React from 'react';
import { BrowserRouter as Router, Route, Switch } from 'react-router-dom';
import CustomerList from './components/CustomerList';
import { CustomerProvider, CustomerDetails } from './components/CustomerDetails';

const App = () => {
  return (
    <Router>
      <Switch>
        <Route path="/" exact component={CustomerList} />
        <CustomerProvider>
          <Route path="/customer/:id" component={CustomerDetails} />
        </CustomerProvider>
        {/* Adicione mais rotas conforme necessário */}
      </Switch>
    </Router>
  );
};

export default App;
