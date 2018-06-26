require 'spec_helper'

describe "XML Transformation" do

  let(:template) { Nokogiri::XSLT(File.read('/home/app/src/main/webapp/xsl/kernel4_to_oaidc.xsl')) }

  describe "Good XML" do
    let(:document) { Nokogiri::XML(File.read('/home/app/spec/fixtures/_10.14279.xml'), nil, 'UTF-8', &:noblanks) }
      it 'generating formatted content' do
        # transformed_document = template.apply_to(document)
        # puts transformed_document
        expect(template.transform(document)).not_to raise_error
      end
  end

  describe "Bad XML" do
    let(:document) { Nokogiri::XML(File.read('/home/app/spec/fixtures/_10.23650.xml'), nil, 'UTF-8', &:noblanks) }
      it 'generating formatted content' do
        # transformed_document = template.apply_to(document)
        # puts transformed_document
        expect(template.transform(document)).to raise_error
      end
  end
end
