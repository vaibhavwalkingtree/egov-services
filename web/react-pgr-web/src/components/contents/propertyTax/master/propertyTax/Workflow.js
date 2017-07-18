import React, {Component} from 'react';
import {connect} from 'react-redux';

import {Grid, Row, Col, Table, DropdownButton} from 'react-bootstrap';
import {Card, CardHeader, CardText} from 'material-ui/Card';
import FloatingActionButton from 'material-ui/FloatingActionButton';
import Chip from 'material-ui/Chip';
import FontIcon from 'material-ui/FontIcon';
import {RadioButton, RadioButtonGroup} from 'material-ui/RadioButton';
import Upload from 'material-ui-upload/Upload';
import FlatButton from 'material-ui/FlatButton';
import TextField from 'material-ui/TextField';
import {blue800, red500,white} from 'material-ui/styles/colors';
import DatePicker from 'material-ui/DatePicker';
import SelectField from 'material-ui/SelectField';
import MenuItem from 'material-ui/MenuItem';
import Checkbox from 'material-ui/Checkbox';
import RaisedButton from 'material-ui/RaisedButton';
import Api from '../../../../../api/api';


var flag = 0;
const styles = {
  errorStyle: {
    color: red500
  },
  underlineStyle: {
    borderColor: "#354f57"
  },
  underlineFocusStyle: {
    borderColor: "#354f57"
  },
  floatingLabelStyle: {
    color: "#354f57"
  },
  floatingLabelFocusStyle: {
    color:"#354f57"
  },
  customWidth: {
    width:100
  },
  checkbox: {
    marginBottom: 0,
    marginTop:15
  },
  uploadButton: {
   verticalAlign: 'middle',
 },
 uploadInput: {
   cursor: 'pointer',
   position: 'absolute',
   top: 0,
   bottom: 0,
   right: 0,
   left: 0,
   width: '100%',
   opacity: 0,
 },
 floatButtonMargin: {
   marginLeft: 20,
   fontSize:12,
   width:30,
   height:30
 },
 iconFont: {
   fontSize:17,
   cursor:'pointer'
 },
 radioButton: {
    marginBottom:0,
  },
actionWidth: {
  width:160
},
reducePadding: {
  paddingTop:4,
  paddingBottom:0
},
noPadding: {
  paddingBottom:0,
  paddingTop:0
},
noMargin: {
  marginBottom: 0
},
textRight: {
  textAlign:'right'
},
chip: {
  marginTop:4
}
};


class Workflow extends Component {

  constructor(props) {
    super(props);
    this.state= {
        designation: [],
        workflowDepartment:[],
          approverName:[],
    }
  } 


  componentDidMount() {
    //call boundary service fetch wards,location,zone data
    var currentThis = this;


    Api.commonApiPost( 'egov-common-masters/departments/_search').then((res)=>{
      console.log(res);
      currentThis.setState({workflowDepartment: res.Department})
    }).catch((err)=> {
      currentThis.setState({
        workflowDepartment:[]
      })
      console.log(err)
    })

     Api.commonApiPost( 'egov-common-workflows/designations/_search?businessKey=Create Property&departmentRule=&currentStatus=&amountRule=&additionalRule=&pendingAction=&approvalDepartmentName=&designation',{}, {},false, true).then((res)=>{
      console.log(res);
      currentThis.setState({designation: res.designation})
    }).catch((err)=> {
      currentThis.setState({
        designation:[]
      })
      console.log(err)
    })

  
  } 

  handleWorkFlowChange = (e, type) => {

    let currentThis = this;

    let query = {};

    let hasData = false;

    if(type == 'department' && e.target.value != '' && this.props.workflow.designation) {
      query = {
          departmentId: e.target.value,
          designationId: this.props.workflow.designation
      }
      hasData = true;

    } else if(type == 'designation' && e.target.value != '' && this.props.workflow.department) {
      query = {
          departmentId: this.props.workflow.department,
          designationId: e.target.value
      }
      hasData = true;
    } else {
      hasData = false;
    }

    if(hasData){
        Api.commonApiPost( '/hr-employee/employees/_search', query).then((res)=>{
         console.log(res);
          currentThis.setState({approverName: res.approverName})
      }).catch((err)=> {
        currentThis.setState({
          approverName:[]
        })
        console.log(err);
      })
    }
  } 


  onFileLoad = (e, file) => {  console.log(file.name)
    this.setState((prevState)=>{
      prevState.files.push(file.name);
    })
    console.log(this.state.files)
  }    


  render() {

    const renderOption = function(list,listName="") {
        if(list)
        {
            return list.map((item)=>
            {
                return (<MenuItem key={item.id} value={item.id} primaryText={item.name}/>)
            })
        }
    }

    let {
      owners,
      workflow,
      fieldErrors,
      isFormValid,
      handleChange,
      handleChangeNextOne,
      handleChangeNextTwo,
      deleteObject,
      deleteNestedObject,
      editObject,
      editIndex,
      isEditIndex,
      isAddRoom
    } = this.props;

    let {search, handleWorkFlowChange} = this;

    let cThis = this;

    return ( 
                  <Card>
                    <CardHeader style={styles.reducePadding}  title={<div style={{color:"#354f57", fontSize:18,margin:'8px 0'}}>Workflow</div>} />
                    <CardText style={styles.reducePadding}>
                        <Card className="darkShadow">
                            <CardText style={styles.reducePadding}>
                                <Grid fluid>
                                    <Row>
                                        <Col xs={12} md={3} sm={6}>
                                              <SelectField  className="fullWidth selectOption"
                                                  floatingLabelText="Department Name"
                                                  errorText={fieldErrors.workflowDepartment ? <span style={{position:"absolute", bottom:-13}}>{fieldErrors.workflowDepartment}</span>: ""}
                                                  value={workflow.workflowDepartment ? workflow.workflowDepartment :""}
                                                  onChange={(event, index, value) => {
                                                    var e = {
                                                      target: {
                                                        value: value
                                                      }
                                                    };
                                                    handleWorkFlowChange(e, 'department');
                                                    handleChange(e, "workflowDepartment", false, "")}}
                                                  floatingLabelFocusStyle={styles.floatingLabelFocusStyle}
                                                  underlineStyle={styles.underlineStyle}
                                                  underlineFocusStyle={styles.underlineFocusStyle}
                                                  floatingLabelStyle={{color:"rgba(0,0,0,0.5)"}}
                                                  >
                                                    {renderOption(this.state.workflowDepartment)}
                                              </SelectField>
                                        </Col>
                                        <Col xs={12} md={3} sm={6}>
                                              <SelectField  className="fullWidth selectOption"
                                                  floatingLabelText="Designation Name"
                                                  errorText={fieldErrors.workflowDesignation ? <span style={{position:"absolute", bottom:-13}}>{fieldErrors.workflowDesignation}</span>: ""}
                                                  value={workflow.workflowDesignation ? workflow.workflowDesignation :""}
                                                  onChange={(event, index, value) => {
                                                    var e = {
                                                      target: {
                                                        value: value
                                                      }
                                                    };
                                                    handleWorkFlowChange(e, 'designation');
                                                    handleChange(e, "workflowDesignation", false, "")}}
                                                  floatingLabelFocusStyle={styles.floatingLabelFocusStyle}
                                                  underlineStyle={styles.underlineStyle}
                                                  underlineFocusStyle={styles.underlineFocusStyle}
                                                  floatingLabelStyle={{color:"rgba(0,0,0,0.5)"}}
                                                  >
                                                   {renderOption(this.state.designation)}
                                              </SelectField>
                                        </Col>
                                        <Col xs={12} md={3} sm={6}>
                                              <SelectField  className="fullWidth selectOption"
                                                  floatingLabelText="Approver Name"
                                                  errorText={fieldErrors.approverName ? <span style={{position:"absolute", bottom:-13}}>{fieldErrors.approverName}</span>: ""}
                                                  value={workflow.approverName ? workflow.approverName :""}
                                                  onChange={(event, index, value) => {
                                                    var e = {
                                                      target: {
                                                        value: value
                                                      }
                                                    };
                                                    handleChange(e, "approverName", false, "")}}
                                                  floatingLabelFocusStyle={styles.floatingLabelFocusStyle}
                                                  underlineStyle={styles.underlineStyle}
                                                  underlineFocusStyle={styles.underlineFocusStyle}
                                                  floatingLabelStyle={{color:"rgba(0,0,0,0.5)"}}
                                                  >
                                                    <MenuItem value={4} primaryText="Options" />
                                              </SelectField>
                                        </Col>
                                    </Row>
                                </Grid>
                            </CardText>
                        </Card>
                    </CardText>
                  </Card>
)
  }

}

const mapStateToProps = state => ({
  workflow:state.form.form,
  fieldErrors: state.form.fieldErrors,
  editIndex: state.form.editIndex,
  addRoom : state.form.addRoom
});

const mapDispatchToProps = dispatch => ({
  initForm: () => {
    dispatch({
      type: "RESET_STATE",
      validationData: {
        required: {
          current: [],
          required: []
        },
        pattern: {
          current: [],
          required: []
        }
      }
    });
  },
  handleChange: (e, property, isRequired, pattern) => {
    dispatch({type: "HANDLE_CHANGE", property, value: e.target.value, isRequired, pattern});
  },

  handleChangeNextOne: (e, property, propertyOne, isRequired, pattern) => {
    dispatch({
      type: "HANDLE_CHANGE_NEXT_ONE",
      property,
      propertyOne,
      value: e.target.value,
      isRequired,
      pattern
    })
  },
  addNestedFormData: (formArray, formData) => {
    dispatch({
      type: "PUSH_ONE",
      formArray,
      formData
    })
  },

  addNestedFormDataTwo: (formObject, formArray, formData) => {
    dispatch({
      type: "PUSH_ONE_ARRAY",
      formObject,
      formArray,
      formData
    })
  },

  deleteObject: (property, index) => {
    dispatch({
      type: "DELETE_OBJECT",
      property,
      index
    })
  },

  deleteNestedObject: (property,propertyOne, index) => {
    dispatch({
      type: "DELETE_NESTED_OBJECT",
      property,
      propertyOne,
      index
    })
  },

  editObject: (objectName, object, isEditable) => {
    dispatch({
      type: "EDIT_OBJECT",
      objectName,
      object,
      isEditable
    })
  },

  resetObject: (object) => {
    dispatch({
      type: "RESET_OBJECT",
      object
    })
  },

  updateObject: (objectName, object) => {
    dispatch({
      type: "UPDATE_OBJECT",
      objectName,
      object
    })
  },

  updateNestedObject:  (objectName, objectArray, object) => {
    dispatch({
      type: "UPDATE_NESTED_OBJECT",
      objectName,
      objectArray,
      object
    })
  },

  isEditIndex: (index) => {
    dispatch({
      type: "EDIT_INDEX",
      index
    })
  },

  isAddRoom: (room) => {
    dispatch({
      type: "ADD_ROOM",
      room
    })
  },

});

export default connect(mapStateToProps, mapDispatchToProps)(Workflow);

