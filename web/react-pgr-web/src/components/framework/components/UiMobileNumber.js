import React, {Component} from 'react';
import TextField from 'material-ui/TextField';

export default class UiMobileField extends Component {
	constructor(props) {
       super(props);
   	}

	renderMobileNumberBox = (item) => {
		switch (this.props.ui) {
			case 'google': 
				return (
					<TextField 
						fullWidth={true} 
						type="number"
						floatingLabelText={item.label + (item.isRequired ? " *" : "")} 
						value={eval(item.jsonpath)}
						disabled={item.isDisabled}
						errorText={this.props.fieldErrors[eval(item.jsonpath)]}
						onChange={(e) => this.props.handler(e, eval(item.jsonpath), item.isRequired ? true : false, '/d{10}', item.requiredErrMsg, item.patternErrMsg)} />
				);
		}
	}

	render () {
		return (
	      <div>
	        {this.renderMobileNumberBox(this.props.item)}
	      </div>
	    );
	}
}